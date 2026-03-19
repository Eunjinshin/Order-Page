'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Eye, EyeOff, Pencil, Trash2, Package } from 'lucide-react';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Pagination from '@/components/ui/Pagination';
import Modal from '@/components/ui/Modal';
import Input from '@/components/ui/Input';
import { PageLoader } from '@/components/ui/Spinner';
import { adminApi } from '@/lib/api/admin';
import { formatPrice } from '@/lib/utils';
import type { Product } from '@/types/product';

export default function AdminProductsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [stockModal, setStockModal] = useState<{ open: boolean; optionId: number | null }>({
    open: false, optionId: null,
  });
  const [stockQty, setStockQty] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-products', page],
    queryFn: () => adminApi.getAllProducts(page, 20),
  });

  const toggleMutation = useMutation({
    mutationFn: ({ id, visible }: { id: number; visible: boolean }) =>
      adminApi.toggleVisibility(id, visible),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-products'] }),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminApi.deleteProduct(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-products'] }),
  });

  const stockMutation = useMutation({
    mutationFn: () =>
      adminApi.updateStock({ optionId: stockModal.optionId!, stockQty: Number(stockQty) }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      setStockModal({ open: false, optionId: null });
      setStockQty('');
    },
  });

  if (isLoading) return <PageLoader />;

  const response = data?.data?.data;
  const products: Product[] = response?.content ?? [];

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <Package className="h-6 w-6" />
          상품 관리
          <span className="text-base font-normal text-gray-400">
            ({response?.totalElements ?? 0}개)
          </span>
        </h1>
      </div>

      <div className="border border-gray-200 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">상품명</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">가격</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">옵션 / 재고</th>
              <th className="text-center px-4 py-3 font-medium text-gray-600">노출</th>
              <th className="text-center px-4 py-3 font-medium text-gray-600">관리</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {products.map((product) => (
              <tr key={product.productId} className="hover:bg-gray-50">
                <td className="px-4 py-3">
                  <p className="font-medium text-gray-900 line-clamp-1">{product.productName}</p>
                  <p className="text-xs text-gray-400">ID: {product.productId}</p>
                </td>
                <td className="px-4 py-3 font-medium">{formatPrice(product.productPrice)}</td>
                <td className="px-4 py-3">
                  <div className="space-y-1">
                    {product.options?.slice(0, 3).map((opt) => (
                      <div key={opt.optionId} className="flex items-center gap-2">
                        <span className="text-gray-600 text-xs">
                          {[opt.color, opt.size].filter(Boolean).join('/') || `옵션${opt.optionId}`}
                        </span>
                        <Badge variant={opt.stockQty === 0 ? 'danger' : opt.stockQty <= (opt.alertThreshold ?? 10) ? 'warning' : 'success'} className="text-xs">
                          {opt.stockQty}개
                        </Badge>
                        <button
                          onClick={() => {
                            setStockModal({ open: true, optionId: opt.optionId });
                            setStockQty(String(opt.stockQty));
                          }}
                          className="text-xs text-blue-500 hover:underline"
                        >
                          수정
                        </button>
                      </div>
                    ))}
                    {(product.options?.length ?? 0) > 3 && (
                      <p className="text-xs text-gray-400">+{product.options!.length - 3}개 더</p>
                    )}
                  </div>
                </td>
                <td className="px-4 py-3 text-center">
                  <button
                    onClick={() =>
                      toggleMutation.mutate({ id: product.productId, visible: !product.isVisible })
                    }
                    className={`transition-colors ${
                      product.isVisible ? 'text-green-500 hover:text-gray-400' : 'text-gray-300 hover:text-green-500'
                    }`}
                    title={product.isVisible ? '비공개로 변경' : '공개로 변경'}
                  >
                    {product.isVisible ? <Eye className="h-5 w-5" /> : <EyeOff className="h-5 w-5" />}
                  </button>
                </td>
                <td className="px-4 py-3 text-center">
                  <button
                    onClick={() => {
                      if (confirm('정말 삭제하시겠습니까?')) deleteMutation.mutate(product.productId);
                    }}
                    className="p-1.5 text-gray-300 hover:text-red-500 transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        currentPage={response?.number ?? 0}
        totalPages={response?.totalPages ?? 1}
        onPageChange={setPage}
        className="mt-6"
      />

      {/* 재고 수정 모달 */}
      <Modal
        open={stockModal.open}
        onClose={() => setStockModal({ open: false, optionId: null })}
        title="재고 수량 수정"
        size="sm"
      >
        <div className="space-y-4">
          <Input
            label="재고 수량"
            type="number"
            min={0}
            value={stockQty}
            onChange={(e) => setStockQty(e.target.value)}
          />
          <div className="flex gap-2">
            <Button variant="outline" className="flex-1" onClick={() => setStockModal({ open: false, optionId: null })}>
              취소
            </Button>
            <Button
              className="flex-1"
              loading={stockMutation.isPending}
              onClick={() => stockMutation.mutate()}
            >
              저장
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
