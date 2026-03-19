'use client';

import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Suspense } from 'react';
import Button from '@/components/ui/Button';
import { PageLoader } from '@/components/ui/Spinner';
import { memberApi } from '@/lib/api/member';
import { couponApi, UserCoupon } from '@/lib/api/coupon';
import { orderApi } from '@/lib/api/order';
import { paymentApi } from '@/lib/api/payment';
import { productApi } from '@/lib/api/product';
import { formatPrice } from '@/lib/utils';
import type { UserAddress } from '@/types/user';
import type { ProductOption } from '@/types/product';

function CheckoutContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // URL 파라미터에서 optionId, qty 파싱
  const orderItems: { optionId: number; quantity: number }[] = [];
  const optionIds = searchParams.getAll('optionId');
  const quantities = searchParams.getAll('qty');
  optionIds.forEach((id, i) => {
    orderItems.push({ optionId: Number(id), quantity: Number(quantities[i] ?? 1) });
  });

  const [selectedAddress, setSelectedAddress] = useState<UserAddress | null>(null);
  const [selectedCoupon, setSelectedCoupon] = useState<UserCoupon | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<'CARD' | 'KAKAO' | 'NAVER' | 'TRANSFER'>('CARD');

  const { data: addressData } = useQuery({
    queryKey: ['addresses'],
    queryFn: () => memberApi.getAddresses(),
    onSuccess: (res: any) => {
      const defaultAddr = res.data?.data?.find((a: UserAddress) => a.isDefault);
      if (defaultAddr) setSelectedAddress(defaultAddr);
    },
  } as any);

  const { data: couponData } = useQuery({
    queryKey: ['coupons'],
    queryFn: () => couponApi.getMyCoupons(),
  });

  const addresses: UserAddress[] = addressData?.data?.data ?? [];
  const coupons: UserCoupon[] = couponData?.data?.data ?? [];

  // 임시 가격 계산 (실제로는 서버에서 검증)
  const [totalPrice, setTotalPrice] = useState(0);

  const discount = selectedCoupon
    ? selectedCoupon.discountType === 'RATE'
      ? Math.floor(totalPrice * (selectedCoupon.discountValue / 100))
      : selectedCoupon.discountValue
    : 0;
  const finalPrice = Math.max(0, totalPrice - discount);

  const createOrder = useMutation({
    mutationFn: async () => {
      const orderRes = await orderApi.createOrder({
        addressId: selectedAddress!.addressId,
        couponId: selectedCoupon?.couponId,
        items: orderItems,
      });
      const order = orderRes.data.data!;

      const paymentRes = await paymentApi.preparePayment({
        orderId: order.orderId,
        paymentMethod,
        pgProvider: 'TOSSPAYMENTS',
      });
      const payment = paymentRes.data.data!;

      await paymentApi.confirmPayment(payment.paymentId);
      return order.orderId;
    },
    onSuccess: (orderId) => {
      router.push(`/orders/${orderId}?success=true`);
    },
  });

  if (orderItems.length === 0) {
    return (
      <div className="text-center py-20 text-gray-400">
        주문할 상품이 없습니다.{' '}
        <button onClick={() => router.push('/')} className="text-blue-600 hover:underline">
          쇼핑 계속하기
        </button>
      </div>
    );
  }

  const paymentMethods = [
    { value: 'CARD', label: '신용/체크카드' },
    { value: 'KAKAO', label: '카카오페이' },
    { value: 'NAVER', label: '네이버페이' },
    { value: 'TRANSFER', label: '계좌이체' },
  ] as const;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">주문서</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="md:col-span-2 space-y-6">
          {/* 배송지 선택 */}
          <section className="border border-gray-200 rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">배송지</h2>
            {addresses.length === 0 ? (
              <div className="text-sm text-gray-500 space-y-2">
                <p>등록된 배송지가 없습니다.</p>
                <Button variant="outline" size="sm" onClick={() => router.push('/mypage/addresses')}>
                  배송지 등록
                </Button>
              </div>
            ) : (
              <div className="space-y-2">
                {addresses.map((addr) => (
                  <label
                    key={addr.addressId}
                    className={`flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${
                      selectedAddress?.addressId === addr.addressId
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <input
                      type="radio"
                      name="address"
                      checked={selectedAddress?.addressId === addr.addressId}
                      onChange={() => setSelectedAddress(addr)}
                      className="mt-0.5"
                    />
                    <div className="text-sm">
                      <p className="font-medium text-gray-900">
                        {addr.addressName}
                        {addr.isDefault && (
                          <span className="ml-2 text-xs text-blue-600 bg-blue-100 px-1.5 py-0.5 rounded">기본</span>
                        )}
                      </p>
                      <p className="text-gray-600 mt-0.5">({addr.zipCode}) {addr.address}</p>
                      {addr.detailAddress && <p className="text-gray-600">{addr.detailAddress}</p>}
                    </div>
                  </label>
                ))}
              </div>
            )}
          </section>

          {/* 쿠폰 선택 */}
          {coupons.length > 0 && (
            <section className="border border-gray-200 rounded-xl p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">쿠폰 적용</h2>
              <select
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={selectedCoupon?.couponId ?? ''}
                onChange={(e) => {
                  const coupon = coupons.find((c) => c.couponId === Number(e.target.value));
                  setSelectedCoupon(coupon ?? null);
                }}
              >
                <option value="">쿠폰 선택 안 함</option>
                {coupons.map((c) => (
                  <option key={c.couponId} value={c.couponId}>
                    {c.name} —{' '}
                    {c.discountType === 'RATE' ? `${c.discountValue}% 할인` : `${formatPrice(c.discountValue)} 할인`}
                    {c.minOrderAmount > 0 && ` (${formatPrice(c.minOrderAmount)} 이상)`}
                  </option>
                ))}
              </select>
            </section>
          )}

          {/* 결제 수단 */}
          <section className="border border-gray-200 rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">결제 수단</h2>
            <div className="grid grid-cols-2 gap-2">
              {paymentMethods.map((method) => (
                <label
                  key={method.value}
                  className={`flex items-center gap-2 p-3 rounded-lg border cursor-pointer transition-colors ${
                    paymentMethod === method.value
                      ? 'border-blue-500 bg-blue-50 text-blue-700'
                      : 'border-gray-200 text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="payment"
                    value={method.value}
                    checked={paymentMethod === method.value}
                    onChange={() => setPaymentMethod(method.value)}
                    className="sr-only"
                  />
                  <span className="text-sm font-medium">{method.label}</span>
                </label>
              ))}
            </div>
          </section>
        </div>

        {/* 결제 요약 */}
        <div className="md:col-span-1">
          <div className="sticky top-24 bg-gray-50 rounded-2xl p-6 space-y-4 border border-gray-200">
            <h3 className="font-semibold text-gray-900">결제 금액</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>상품 금액</span>
                <span>{formatPrice(totalPrice)}</span>
              </div>
              {selectedCoupon && (
                <div className="flex justify-between text-green-600">
                  <span>쿠폰 할인</span>
                  <span>-{formatPrice(discount)}</span>
                </div>
              )}
              <div className="flex justify-between text-gray-600">
                <span>배송비</span>
                <span className="text-green-600">무료</span>
              </div>
            </div>
            <div className="border-t border-gray-200 pt-4 flex justify-between font-bold">
              <span>최종 결제</span>
              <span className="text-blue-600 text-lg">{formatPrice(finalPrice)}</span>
            </div>

            {createOrder.isError && (
              <p className="text-xs text-red-600 text-center">
                주문 처리 중 오류가 발생했습니다.
              </p>
            )}

            <Button
              size="lg"
              className="w-full"
              disabled={!selectedAddress}
              loading={createOrder.isPending}
              onClick={() => createOrder.mutate()}
            >
              {formatPrice(finalPrice)} 결제하기
            </Button>

            {!selectedAddress && (
              <p className="text-xs text-center text-gray-400">배송지를 선택해주세요.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default function CheckoutPage() {
  return (
    <Suspense fallback={<PageLoader />}>
      <CheckoutContent />
    </Suspense>
  );
}
