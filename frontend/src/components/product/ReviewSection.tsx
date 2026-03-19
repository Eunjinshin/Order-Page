'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Star, ThumbsUp } from 'lucide-react';
import { reviewApi } from '@/lib/api/review';
import Pagination from '@/components/ui/Pagination';
import { formatDate } from '@/lib/utils';
import { useAuthStore } from '@/store/authStore';

interface ReviewSectionProps {
  productId: number;
}

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex gap-0.5">
      {[1, 2, 3, 4, 5].map((n) => (
        <Star
          key={n}
          className={`h-4 w-4 ${n <= rating ? 'text-yellow-400 fill-yellow-400' : 'text-gray-200 fill-gray-200'}`}
        />
      ))}
    </div>
  );
}

export default function ReviewSection({ productId }: ReviewSectionProps) {
  const [page, setPage] = useState(0);
  const { user } = useAuthStore();
  const queryClient = useQueryClient();

  const { data } = useQuery({
    queryKey: ['reviews', productId, page],
    queryFn: () => reviewApi.getProductReviews(productId, page, 5),
  });

  const helpfulMutation = useMutation({
    mutationFn: (reviewId: number) => reviewApi.markHelpful(reviewId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['reviews', productId] }),
  });

  const reviews = data?.data?.data;

  return (
    <section className="border-t border-gray-200 pt-8">
      <h2 className="text-xl font-bold text-gray-900 mb-6">
        상품 리뷰
        {reviews && (
          <span className="text-base font-normal text-gray-400 ml-2">
            ({reviews.totalElements}건)
          </span>
        )}
      </h2>

      {(reviews?.content?.length ?? 0) === 0 ? (
        <div className="text-center py-12 text-gray-400">
          아직 리뷰가 없습니다. 첫 번째 리뷰를 작성해보세요!
        </div>
      ) : (
        <div className="space-y-6">
          {reviews!.content.map((review) => (
            <div key={review.reviewId} className="border border-gray-100 rounded-xl p-5 space-y-3">
              <div className="flex items-start justify-between">
                <div className="space-y-1">
                  <StarRating rating={review.rating} />
                  <p className="text-xs text-gray-400">{formatDate(review.createdAt)}</p>
                </div>
                {user && (
                  <button
                    onClick={() => helpfulMutation.mutate(review.reviewId)}
                    className="flex items-center gap-1 text-xs text-gray-400 hover:text-blue-500 transition-colors"
                  >
                    <ThumbsUp className="h-3.5 w-3.5" />
                    도움됨 {review.helpfulCount > 0 && `(${review.helpfulCount})`}
                  </button>
                )}
              </div>

              <p className="text-sm text-gray-700 leading-relaxed">{review.content}</p>

              {review.images?.length > 0 && (
                <div className="flex gap-2 flex-wrap">
                  {review.images.map((img) => (
                    <div key={img.imageId} className="relative w-16 h-16 rounded-lg overflow-hidden bg-gray-100">
                      <img src={img.imageUrl} alt="" className="w-full h-full object-cover" />
                    </div>
                  ))}
                </div>
              )}

              {review.reply && (
                <div className="bg-gray-50 rounded-lg p-3 border-l-4 border-blue-400">
                  <p className="text-xs font-medium text-blue-600 mb-1">판매자 답변</p>
                  <p className="text-sm text-gray-700">{review.reply.content}</p>
                </div>
              )}
            </div>
          ))}

          <Pagination
            currentPage={reviews!.number}
            totalPages={reviews!.totalPages}
            onPageChange={setPage}
          />
        </div>
      )}
    </section>
  );
}
