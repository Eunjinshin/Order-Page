'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { Trash2, ShoppingCart } from 'lucide-react';
import Button from '@/components/ui/Button';
import { PageLoader } from '@/components/ui/Spinner';
import { cartApi } from '@/lib/api/cart';
import { formatPrice } from '@/lib/utils';

export default function CartPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [selected, setSelected] = useState<Set<number>>(new Set());

  const { data, isLoading } = useQuery({
    queryKey: ['cart'],
    queryFn: () => cartApi.getCart(),
    onSuccess: (res) => {
      const ids = res.data.data?.map((item: any) => item.cartId) ?? [];
      setSelected(new Set(ids));
    },
  } as any);

  const items = data?.data?.data ?? [];

  const updateMutation = useMutation({
    mutationFn: ({ cartId, quantity }: { cartId: number; quantity: number }) =>
      cartApi.updateCartItem(cartId, quantity),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });

  const deleteMutation = useMutation({
    mutationFn: (cartId: number) => cartApi.deleteCartItem(cartId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });

  const clearMutation = useMutation({
    mutationFn: () => cartApi.clearCart(),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });

  const toggleSelect = (id: number) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const toggleAll = () => {
    if (selected.size === items.length) setSelected(new Set());
    else setSelected(new Set(items.map((i: any) => i.cartId)));
  };

  const selectedItems = items.filter((i: any) => selected.has(i.cartId));
  const totalPrice = selectedItems.reduce(
    (sum: number, item: any) => sum + item.unitPrice * item.quantity,
    0
  );

  const handleCheckout = () => {
    if (selectedItems.length === 0) return;
    const params = selectedItems
      .map((i: any) => `optionId=${i.optionId}&qty=${i.quantity}`)
      .join('&');
    router.push(`/checkout?${params}`);
  };

  if (isLoading) return <PageLoader />;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
        <ShoppingCart className="h-6 w-6" />
        장바구니
        <span className="text-base font-normal text-gray-400">({items.length})</span>
      </h1>

      {items.length === 0 ? (
        <div className="text-center py-20 space-y-4">
          <ShoppingCart className="h-16 w-16 text-gray-200 mx-auto" />
          <p className="text-gray-400">장바구니가 비어있습니다.</p>
          <Button onClick={() => router.push('/')}>쇼핑 계속하기</Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* 아이템 목록 */}
          <div className="md:col-span-2 space-y-3">
            {/* 전체 선택 */}
            <div className="flex items-center justify-between py-2 border-b border-gray-200">
              <label className="flex items-center gap-2 text-sm font-medium text-gray-700 cursor-pointer">
                <input
                  type="checkbox"
                  checked={selected.size === items.length && items.length > 0}
                  onChange={toggleAll}
                  className="rounded"
                />
                전체 선택 ({selected.size}/{items.length})
              </label>
              <button
                onClick={() => clearMutation.mutate()}
                className="text-xs text-gray-400 hover:text-red-500 transition-colors"
              >
                전체 삭제
              </button>
            </div>

            {items.map((item: any) => (
              <div key={item.cartId} className="flex gap-4 p-4 border border-gray-200 rounded-xl">
                <input
                  type="checkbox"
                  checked={selected.has(item.cartId)}
                  onChange={() => toggleSelect(item.cartId)}
                  className="mt-1 rounded"
                />
                <div className="relative w-20 h-20 flex-shrink-0 rounded-lg overflow-hidden bg-gray-100">
                  {item.imageUrl && (
                    <Image src={item.imageUrl} alt={item.productName} fill className="object-cover" sizes="80px" />
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-900 line-clamp-1">{item.productName}</p>
                  <p className="text-sm text-gray-500 mt-0.5">
                    {[item.color, item.size].filter(Boolean).join(' / ')}
                  </p>
                  <p className="text-sm font-bold text-gray-900 mt-1">{formatPrice(item.unitPrice)}</p>
                  <div className="flex items-center gap-2 mt-2">
                    <div className="flex items-center border border-gray-200 rounded-lg">
                      <button
                        onClick={() => updateMutation.mutate({ cartId: item.cartId, quantity: Math.max(1, item.quantity - 1) })}
                        className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-50 rounded-l-lg text-sm"
                      >
                        -
                      </button>
                      <span className="w-8 text-center text-sm">{item.quantity}</span>
                      <button
                        onClick={() => updateMutation.mutate({ cartId: item.cartId, quantity: item.quantity + 1 })}
                        className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-50 rounded-r-lg text-sm"
                      >
                        +
                      </button>
                    </div>
                    <span className="text-sm font-semibold text-blue-600">
                      {formatPrice(item.unitPrice * item.quantity)}
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => deleteMutation.mutate(item.cartId)}
                  className="text-gray-300 hover:text-red-400 transition-colors self-start"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            ))}
          </div>

          {/* 결제 요약 */}
          <div className="md:col-span-1">
            <div className="sticky top-24 bg-gray-50 rounded-2xl p-6 space-y-4 border border-gray-200">
              <h3 className="font-semibold text-gray-900">주문 요약</h3>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between text-gray-600">
                  <span>선택 상품 ({selectedItems.length})</span>
                  <span>{formatPrice(totalPrice)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>배송비</span>
                  <span className="text-green-600">무료</span>
                </div>
              </div>
              <div className="border-t border-gray-200 pt-4 flex justify-between font-bold">
                <span>합계</span>
                <span className="text-blue-600 text-lg">{formatPrice(totalPrice)}</span>
              </div>
              <Button
                size="lg"
                className="w-full"
                disabled={selectedItems.length === 0}
                onClick={handleCheckout}
              >
                주문하기 ({selectedItems.length}건)
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
