'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ShoppingBag } from 'lucide-react';
import Badge, { orderStateBadge } from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Pagination from '@/components/ui/Pagination';
import Modal from '@/components/ui/Modal';
import Input from '@/components/ui/Input';
import { PageLoader } from '@/components/ui/Spinner';
import { adminApi } from '@/lib/api/admin';
import { formatPrice, formatDate } from '@/lib/utils';
import type { Order } from '@/types/order';

const ORDER_STATES = ['PENDING', 'PAID', 'PREPARING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

export default function AdminOrdersPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [returnPage, setReturnPage] = useState(0);
  const [trackingModal, setTrackingModal] = useState<{ open: boolean; orderId: number | null }>({
    open: false, orderId: null,
  });
  const [carrier, setCarrier] = useState('');
  const [trackingNumber, setTrackingNumber] = useState('');
  const [tab, setTab] = useState<'orders' | 'returns'>('orders');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page],
    queryFn: () => adminApi.getAllOrders(page, 20),
  });

  const { data: returnsData } = useQuery({
    queryKey: ['admin-returns', returnPage],
    queryFn: () => adminApi.getReturnRequests(returnPage),
    enabled: tab === 'returns',
  });

  const stateMutation = useMutation({
    mutationFn: ({ orderId, state }: { orderId: number; state: string }) =>
      adminApi.changeOrderState(orderId, state),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-orders'] }),
  });

  const trackingMutation = useMutation({
    mutationFn: () =>
      adminApi.registerTracking(trackingModal.orderId!, carrier, trackingNumber),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-orders'] });
      setTrackingModal({ open: false, orderId: null });
      setCarrier('');
      setTrackingNumber('');
    },
  });

  const returnMutation = useMutation({
    mutationFn: ({ returnId, approve }: { returnId: number; approve: boolean }) =>
      adminApi.processReturn(returnId, approve),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-returns'] }),
  });

  if (isLoading && tab === 'orders') return <PageLoader />;

  const response = data?.data?.data;
  const orders: Order[] = response?.content ?? [];
  const returns = returnsData?.data?.data;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
        <ShoppingBag className="h-6 w-6" />
        주문 관리
      </h1>

      {/* 탭 */}
      <div className="flex gap-1 border-b border-gray-200 mb-6">
        {[
          { key: 'orders', label: '주문 목록' },
          { key: 'returns', label: '반품/교환 요청' },
        ].map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setTab(key as any)}
            className={`px-4 py-2.5 text-sm font-medium border-b-2 transition-colors ${
              tab === key
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {tab === 'orders' ? (
        <>
          <div className="border border-gray-200 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">주문번호</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">주문일시</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">금액</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">상태</th>
                  <th className="text-center px-4 py-3 font-medium text-gray-600">상태 변경</th>
                  <th className="text-center px-4 py-3 font-medium text-gray-600">송장</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {orders.map((order) => {
                  const { label, variant } = orderStateBadge(order.orderState);
                  return (
                    <tr key={order.orderId} className="hover:bg-gray-50">
                      <td className="px-4 py-3 font-medium text-gray-900">{order.orderId}</td>
                      <td className="px-4 py-3 text-gray-600">
                        {formatDate(order.orderedAt.toString())}
                      </td>
                      <td className="px-4 py-3 font-medium">{formatPrice(order.finalPrice)}</td>
                      <td className="px-4 py-3">
                        <Badge variant={variant}>{label}</Badge>
                      </td>
                      <td className="px-4 py-3 text-center">
                        <select
                          className="text-xs border border-gray-200 rounded px-2 py-1 focus:outline-none focus:ring-1 focus:ring-blue-400"
                          value={order.orderState}
                          onChange={(e) =>
                            stateMutation.mutate({ orderId: order.orderId, state: e.target.value })
                          }
                        >
                          {ORDER_STATES.map((s) => (
                            <option key={s} value={s}>
                              {orderStateBadge(s).label}
                            </option>
                          ))}
                        </select>
                      </td>
                      <td className="px-4 py-3 text-center">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => setTrackingModal({ open: true, orderId: order.orderId })}
                        >
                          등록
                        </Button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          <Pagination
            currentPage={response?.number ?? 0}
            totalPages={response?.totalPages ?? 1}
            onPageChange={setPage}
            className="mt-6"
          />
        </>
      ) : (
        <div className="border border-gray-200 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="text-left px-4 py-3 font-medium text-gray-600">요청 ID</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">유형</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">사유</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">상태</th>
                <th className="text-center px-4 py-3 font-medium text-gray-600">처리</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {(returns?.content ?? []).map((req: any) => (
                <tr key={req.returnId} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{req.returnId}</td>
                  <td className="px-4 py-3">
                    <Badge variant={req.type === 'RETURN' ? 'warning' : 'info'}>
                      {req.type === 'RETURN' ? '반품' : '교환'}
                    </Badge>
                  </td>
                  <td className="px-4 py-3 text-gray-600 max-w-xs truncate">{req.reason}</td>
                  <td className="px-4 py-3">
                    <Badge
                      variant={
                        req.status === 'APPROVED'
                          ? 'success'
                          : req.status === 'REJECTED'
                          ? 'danger'
                          : 'warning'
                      }
                    >
                      {req.status}
                    </Badge>
                  </td>
                  <td className="px-4 py-3 text-center">
                    {req.status === 'REQUESTED' && (
                      <div className="flex gap-1 justify-center">
                        <Button
                          size="sm"
                          onClick={() => returnMutation.mutate({ returnId: req.returnId, approve: true })}
                        >
                          승인
                        </Button>
                        <Button
                          size="sm"
                          variant="danger"
                          onClick={() => returnMutation.mutate({ returnId: req.returnId, approve: false })}
                        >
                          거절
                        </Button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* 송장 등록 모달 */}
      <Modal
        open={trackingModal.open}
        onClose={() => setTrackingModal({ open: false, orderId: null })}
        title="송장 등록"
        size="sm"
      >
        <div className="space-y-4">
          <Input label="배송사" placeholder="CJ대한통운" value={carrier} onChange={(e) => setCarrier(e.target.value)} />
          <Input label="운송장 번호" placeholder="123456789012" value={trackingNumber} onChange={(e) => setTrackingNumber(e.target.value)} />
          <div className="flex gap-2">
            <Button variant="outline" className="flex-1" onClick={() => setTrackingModal({ open: false, orderId: null })}>
              취소
            </Button>
            <Button
              className="flex-1"
              loading={trackingMutation.isPending}
              disabled={!carrier || !trackingNumber}
              onClick={() => trackingMutation.mutate()}
            >
              등록
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
