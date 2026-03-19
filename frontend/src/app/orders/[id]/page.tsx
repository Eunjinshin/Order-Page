'use client';

import { useRouter, useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { CheckCircle, Package, Truck } from 'lucide-react';
import { Suspense } from 'react';
import Badge, { orderStateBadge, deliveryStateBadge } from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import { PageLoader } from '@/components/ui/Spinner';
import { orderApi } from '@/lib/api/order';
import { paymentApi } from '@/lib/api/payment';
import { formatPrice, formatDate } from '@/lib/utils';

function OrderDetailContent({ orderId }: { orderId: number }) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const isSuccess = searchParams.get('success') === 'true';

  const { data: orderRes, isLoading: orderLoading } = useQuery({
    queryKey: ['order', orderId],
    queryFn: () => orderApi.getOrder(orderId),
  });

  const { data: paymentRes } = useQuery({
    queryKey: ['payment', orderId],
    queryFn: () => paymentApi.getPayment(orderId),
    retry: false,
  });

  if (orderLoading) return <PageLoader />;

  const order = orderRes?.data?.data;
  const payment = paymentRes?.data?.data;

  if (!order) {
    return (
      <div className="text-center py-20 text-gray-400">
        <p>주문을 찾을 수 없습니다.</p>
        <Button className="mt-4" onClick={() => router.push('/orders')}>
          주문 내역으로
        </Button>
      </div>
    );
  }

  const { label, variant } = orderStateBadge(order.orderState);

  return (
    <div className="max-w-2xl mx-auto px-4 py-8 space-y-6">
      {/* 성공 배너 */}
      {isSuccess && (
        <div className="flex items-center gap-3 bg-green-50 border border-green-200 rounded-xl px-5 py-4">
          <CheckCircle className="h-6 w-6 text-green-500 flex-shrink-0" />
          <div>
            <p className="font-semibold text-green-800">주문이 완료되었습니다!</p>
            <p className="text-sm text-green-600">주문 번호: {order.orderId}</p>
          </div>
        </div>
      )}

      {/* 주문 상태 */}
      <div className="border border-gray-200 rounded-xl p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Package className="h-5 w-5" />
            주문 상세
          </h2>
          <Badge variant={variant}>{label}</Badge>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div>
            <p className="text-gray-500">주문 번호</p>
            <p className="font-medium text-gray-900">{order.orderId}</p>
          </div>
          <div>
            <p className="text-gray-500">주문 일시</p>
            <p className="font-medium text-gray-900">{formatDate(order.orderedAt.toString())}</p>
          </div>
        </div>
      </div>

      {/* 주문 상품 */}
      <div className="border border-gray-200 rounded-xl p-6 space-y-4">
        <h3 className="font-semibold text-gray-900">주문 상품</h3>
        {order.items.map((item) => (
          <div key={item.orderItemId} className="flex justify-between text-sm py-2 border-b border-gray-100 last:border-0">
            <div>
              <p className="font-medium text-gray-900">{item.productName}</p>
              <p className="text-gray-500 text-xs mt-0.5">수량: {item.quantity}개</p>
            </div>
            <p className="font-semibold text-gray-900">{formatPrice(item.unitPrice * item.quantity)}</p>
          </div>
        ))}
      </div>

      {/* 결제 정보 */}
      <div className="border border-gray-200 rounded-xl p-6 space-y-3">
        <h3 className="font-semibold text-gray-900">결제 정보</h3>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between text-gray-600">
            <span>상품 금액</span>
            <span>{formatPrice(order.totalPrice)}</span>
          </div>
          {order.discountPrice > 0 && (
            <div className="flex justify-between text-green-600">
              <span>할인 금액</span>
              <span>-{formatPrice(order.discountPrice)}</span>
            </div>
          )}
          <div className="flex justify-between text-gray-600">
            <span>배송비</span>
            <span className="text-green-600">무료</span>
          </div>
          <div className="flex justify-between font-bold text-gray-900 border-t border-gray-100 pt-2">
            <span>최종 결제</span>
            <span className="text-blue-600 text-base">{formatPrice(order.finalPrice)}</span>
          </div>
          {payment && (
            <div className="flex justify-between text-gray-500 text-xs">
              <span>결제 수단</span>
              <span>{payment.paymentMethod}</span>
            </div>
          )}
        </div>
      </div>

      <div className="flex gap-3">
        <Button variant="outline" className="flex-1" onClick={() => router.push('/orders')}>
          주문 내역
        </Button>
        <Button className="flex-1" onClick={() => router.push('/')}>
          쇼핑 계속하기
        </Button>
      </div>
    </div>
  );
}

export default function OrderDetailPage({ params }: { params: { id: string } }) {
  return (
    <Suspense fallback={<PageLoader />}>
      <OrderDetailContent orderId={Number(params.id)} />
    </Suspense>
  );
}
