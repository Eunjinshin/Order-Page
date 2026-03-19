'use client';

import { useState } from 'react';
import Image from 'next/image';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ShoppingCart, Star } from 'lucide-react';
import Button from '@/components/ui/Button';
import Badge from '@/components/ui/Badge';
import { PageLoader } from '@/components/ui/Spinner';
import ReviewSection from '@/components/product/ReviewSection';
import { productApi } from '@/lib/api/product';
import { cartApi } from '@/lib/api/cart';
import { formatPrice } from '@/lib/utils';
import type { ProductOption } from '@/types/product';

interface ProductDetailProps {
  productId: number;
}

export default function ProductDetail({ productId }: ProductDetailProps) {
  const queryClient = useQueryClient();
  const [selectedOption, setSelectedOption] = useState<ProductOption | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [activeImage, setActiveImage] = useState(0);
  const [cartSuccess, setCartSuccess] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => productApi.getProduct(productId),
  });

  const addToCart = useMutation({
    mutationFn: () => cartApi.addToCart({ optionId: selectedOption!.optionId, quantity }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      setCartSuccess(true);
      setTimeout(() => setCartSuccess(false), 2000);
    },
  });

  if (isLoading) return <PageLoader />;

  const product = data?.data?.data;
  if (!product) return <div className="text-center py-20 text-gray-400">상품을 찾을 수 없습니다.</div>;

  const sortedImages = [...(product.images ?? [])].sort((a, b) => a.sortOrder - b.sortOrder);

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-12">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        {/* 이미지 섹션 */}
        <div className="space-y-3">
          <div className="relative aspect-square rounded-2xl overflow-hidden bg-gray-100">
            {sortedImages[activeImage] ? (
              <Image
                src={sortedImages[activeImage].imageUrl}
                alt={product.productName}
                fill
                className="object-cover"
                sizes="(max-width: 768px) 100vw, 50vw"
              />
            ) : (
              <div className="absolute inset-0 flex items-center justify-center text-gray-300">
                <svg className="h-24 w-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            )}
          </div>
          {sortedImages.length > 1 && (
            <div className="flex gap-2 overflow-x-auto pb-1">
              {sortedImages.map((img, idx) => (
                <button
                  key={img.imageId}
                  onClick={() => setActiveImage(idx)}
                  className={`relative flex-shrink-0 w-16 h-16 rounded-lg overflow-hidden border-2 transition-colors ${
                    idx === activeImage ? 'border-blue-500' : 'border-transparent'
                  }`}
                >
                  <Image src={img.imageUrl} alt="" fill className="object-cover" sizes="64px" />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* 상품 정보 섹션 */}
        <div className="space-y-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{product.productName}</h1>
            <p className="text-3xl font-bold text-blue-600 mt-2">{formatPrice(product.productPrice)}</p>
          </div>

          {product.productDescription && (
            <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">
              {product.productDescription}
            </p>
          )}

          {/* 옵션 선택 */}
          {(product.options?.length ?? 0) > 0 && (
            <div className="space-y-3">
              <p className="text-sm font-medium text-gray-700">옵션 선택</p>
              <div className="grid grid-cols-2 gap-2">
                {product.options!.map((opt) => {
                  const isSelected = selectedOption?.optionId === opt.optionId;
                  const isSoldOut = opt.stockQty === 0;
                  return (
                    <button
                      key={opt.optionId}
                      onClick={() => !isSoldOut && setSelectedOption(opt)}
                      disabled={isSoldOut}
                      className={`relative px-3 py-2.5 rounded-lg border text-sm text-left transition-colors ${
                        isSoldOut
                          ? 'border-gray-200 text-gray-300 cursor-not-allowed bg-gray-50'
                          : isSelected
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-gray-300 text-gray-700 hover:border-blue-400'
                      }`}
                    >
                      <span className="font-medium">
                        {[opt.color, opt.size].filter(Boolean).join(' / ') || `옵션 ${opt.optionId}`}
                      </span>
                      {isSoldOut && (
                        <Badge variant="gray" className="absolute top-1 right-1 text-xs">
                          품절
                        </Badge>
                      )}
                      {!isSoldOut && (
                        <span className="block text-xs text-gray-400 mt-0.5">재고: {opt.stockQty}개</span>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* 수량 */}
          {selectedOption && (
            <div className="flex items-center gap-3">
              <span className="text-sm font-medium text-gray-700">수량</span>
              <div className="flex items-center border border-gray-300 rounded-lg">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="w-9 h-9 flex items-center justify-center text-gray-600 hover:bg-gray-50 rounded-l-lg"
                >
                  -
                </button>
                <span className="w-10 text-center text-sm font-medium">{quantity}</span>
                <button
                  onClick={() => setQuantity(Math.min(selectedOption.stockQty, quantity + 1))}
                  className="w-9 h-9 flex items-center justify-center text-gray-600 hover:bg-gray-50 rounded-r-lg"
                >
                  +
                </button>
              </div>
            </div>
          )}

          {/* 총 금액 */}
          {selectedOption && (
            <div className="flex items-center justify-between py-3 border-t border-gray-100">
              <span className="text-sm text-gray-600">총 금액</span>
              <span className="text-xl font-bold text-blue-600">
                {formatPrice(product.productPrice * quantity)}
              </span>
            </div>
          )}

          {/* 버튼 */}
          <div className="flex gap-3">
            <Button
              size="lg"
              className="flex-1"
              disabled={!selectedOption}
              loading={addToCart.isPending}
              onClick={() => addToCart.mutate()}
            >
              <ShoppingCart className="h-5 w-5 mr-2" />
              {cartSuccess ? '추가됨!' : '장바구니 담기'}
            </Button>
          </div>

          {!selectedOption && (
            <p className="text-sm text-center text-gray-400">옵션을 먼저 선택해주세요.</p>
          )}
        </div>
      </div>

      {/* 리뷰 섹션 */}
      <ReviewSection productId={productId} />
    </div>
  );
}
