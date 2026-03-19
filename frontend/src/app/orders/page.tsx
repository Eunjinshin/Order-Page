'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { Package } from 'lucide-react';
import Badge, { orderStateBadge } from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Pagination from '@/components/ui/Pagination';
import { PageLoader } from '@/components/ui/Spinner';
import Modal from '@/components/ui/Modal';
import { orderApi } from '@/lib/api/order';
import { formatPrice, formatDate } from '@/lib/utils';
import type { Order } from '@/types/order';

export default function OrdersPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [returnModal, setReturnModal] = useState<{ open: boolean; orderItemId: number | null }>({
    open: false,
    orderItemId: null,
  });
  const [returnType, setReturnType] = useState<'RETURN' | 'EXCHANGE'>('RETURN');
  const [returnReason, setReturnReason] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['orders', page],
    queryFn: () => orderApi.getMyOrders(page, 10),
  });

  const cancelMutation = useMutation({
    mutationFn: (orderId: number) => orderApi.cancelOrder(orderId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['orders'] }),
  });

  const returnMutation = useMutation({
    mutationFn: () =>
      orderApi.requestReturn(returnModal.orderItemId!, {
        type: returnType,
        reason: returnReason,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      setReturnModal({ open: false, orderItemId: null });
      setReturnReason('');
    },
  });

  if (isLoading) return <PageLoader />;

  const response = data?.data?.data;
  const orders: Order[] = response?.content ?? [];

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
        <Package className="h-6 w-6" />
        주문 내역
      </h1>

      {orders.length === 0 ? (
        <div className="text-center py-20 space-y-4">
          <Package className="h-16 w-16 text-gray-200 mx-auto" />
          <p className="text-gray-400">주문 내역이 없습니다.</p>
          <Button onClick={() => router.push('/')}>쇼핑하러 가기</Button>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const { label, variant } = orderStateBadge(order.orderState);
            const cancellable = ['PENDING', 'PAID', 'PREPARING'].includes(order.orderState);
            const deliverable = order.orderState === 'DELIVERED';

            return (
              <div
                key={order.orderId}
                className="border border-gray-200 rounded-xl overflow-hidden"
              >
                {/* 주문 헤더 */}
                <div className="flex items-center justify-between px-5 py-4 bg-gray-50 border-b border-gray-200">
                  <div className="space-y-0.5">
                    <p className="text-xs text-gray-500">{formatDate(order.orderedAt.toString())}</p>
                    <p className="text-sm font-medium text-gray-700">주문번호: {order.orderId}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={variant}>{label}</Badge>
                    <button
                      onClick={() => router.push(`/orders/${order.orderId}`)}
                      className="text-xs text-blue-600 hover:underline"
                    >
                      상세보기
                    </button>
                  </div>
                </div>

                {/* 주문 아이템 */}
                <div className="px-5 py-4 space-y-3">
                  {order.items.map((item) => (
                    <div key={item.orderItemId} className="flex items-start justify-between gap-4">
                      <div className="text-sm">
                        <p className="font-medium text-gray-900">{item.productName}</p>
                        <p className="text-gray-500 text-xs mt-0.5">
                          수량: {item.quantity}개 / {formatPrice(item.unitPrice)}
                        </p>
                      </div>
                      {deliverable && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() =>
                            setReturnModal({ open: true, orderItemId: item.orderItemId })
                          }
                        >
                          반품/교환
                        </Button>
                      )}
                    </div>
                  ))}
                </div>

                {/* 주문 푸터 */}
                <div className="flex items-center justify-between px-5 py-3 border-t border-gray-100">
                  <p className="text-sm font-bold text-gray-900">
                    {formatPrice(order.finalPrice)}
                    {order.discountPrice > 0 && (
                      <span className="text-xs font-normal text-green-600 ml-1">
                        ({formatPrice(order.discountPrice)} 할인)
                      </span>
                    )}
                  </p>
                  {cancellable && (
                    <Button
                      variant="danger"
                      size="sm"
                      loading={cancelMutation.isPending}
                      onClick={() => cancelMutation.mutate(order.orderId)}
                    >
                      주문 취소
                    </Button>
                  )}
                </div>
              </div>
            );
          })}

          <Pagination
            currentPage={response?.number ?? 0}
            totalPages={response?.totalPages ?? 1}
            onPageChange={setPage}
          />
        </div>
      )}

      {/* 반품/교환 모달 */}
      <Modal
        open={returnModal.open}
        onClose={() => setReturnModal({ open: false, orderItemId: null })}
        title="반품/교환 신청"
        size="sm"
      >
        <div className="space-y-4">
          <div className="flex gap-3">
            {(['RETURN', 'EXCHANGE'] as const).map((type) => (
              <label
                key={type}
                className={`flex-1 text-center py-2 rounded-lg border cursor-pointer text-sm font-medium transition-colors ${
                  returnType === type
                    ? 'border-blue-500 bg-blue-50 text-blue-700'
                    : 'border-gray-200 text-gray-700'
                }`}
              >
                <input
                  type="radio"
                  value={type}
                  checked={returnType === type}
                  onChange={() => setReturnType(type)}
                  className="sr-only"
                />
                {type === 'RETURN' ? '반품' : '교환'}
              </label>
            ))}
          </div>
          <textarea
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm h-24 resize-none focus:outline-none focus:ring-2 focus:ring-blue-200"
            placeholder="사유를 입력해주세요"
            value={returnReason}
            onChange={(e) => setReturnReason(e.target.value)}
          />
          <div className="flex gap-2">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => setReturnModal({ open: false, orderItemId: null })}
            >
              취소
            </Button>
            <Button
              className="flex-1"
              loading={returnMutation.isPending}
              disabled={!returnReason.trim()}
              onClick={() => returnMutation.mutate()}
            >
              신청하기
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
