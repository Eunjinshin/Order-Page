'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { MessageSquare, Star, Eye, EyeOff, MessageCircle } from 'lucide-react';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Pagination from '@/components/ui/Pagination';
import Modal from '@/components/ui/Modal';
import { PageLoader } from '@/components/ui/Spinner';
import { adminApi } from '@/lib/api/admin';
import { formatDate } from '@/lib/utils';
import type { Review } from '@/lib/api/review';

export default function AdminReviewsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [replyModal, setReplyModal] = useState<{ open: boolean; reviewId: number | null }>({
    open: false, reviewId: null,
  });
  const [replyContent, setReplyContent] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-reviews', page],
    queryFn: () => adminApi.getAllReviews(page, 20),
  });

  const blindMutation = useMutation({
    mutationFn: (reviewId: number) => adminApi.blindReview(reviewId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-reviews'] }),
  });

  const unblindMutation = useMutation({
    mutationFn: (reviewId: number) => adminApi.unblindReview(reviewId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-reviews'] }),
  });

  const replyMutation = useMutation({
    mutationFn: () => adminApi.addReply(replyModal.reviewId!, replyContent),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-reviews'] });
      setReplyModal({ open: false, reviewId: null });
      setReplyContent('');
    },
  });

  if (isLoading) return <PageLoader />;

  const response = data?.data?.data;
  const reviews: Review[] = response?.content ?? [];

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
        <MessageSquare className="h-6 w-6" />
        리뷰 관리
        <span className="text-base font-normal text-gray-400">
          ({response?.totalElements ?? 0}건)
        </span>
      </h1>

      <div className="space-y-3">
        {reviews.map((review) => (
          <div
            key={review.reviewId}
            className={`border rounded-xl p-5 ${
              review.isBlind ? 'border-red-200 bg-red-50' : 'border-gray-200'
            }`}
          >
            <div className="flex items-start justify-between gap-4">
              <div className="flex-1 space-y-2">
                <div className="flex items-center gap-2 flex-wrap">
                  <div className="flex gap-0.5">
                    {[1, 2, 3, 4, 5].map((n) => (
                      <Star
                        key={n}
                        className={`h-3.5 w-3.5 ${
                          n <= review.rating
                            ? 'text-yellow-400 fill-yellow-400'
                            : 'text-gray-200 fill-gray-200'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-xs text-gray-400">상품 ID: {review.productId}</span>
                  <span className="text-xs text-gray-400">{formatDate(review.createdAt)}</span>
                  {review.isBlind && <Badge variant="danger">블라인드</Badge>}
                </div>
                <p className="text-sm text-gray-700">{review.content}</p>
                {review.reply && (
                  <div className="bg-gray-100 rounded-lg p-3 border-l-4 border-blue-400">
                    <p className="text-xs font-medium text-blue-600 mb-1">판매자 답변</p>
                    <p className="text-sm text-gray-700">{review.reply.content}</p>
                  </div>
                )}
              </div>

              {/* 관리 버튼 */}
              <div className="flex gap-1.5 flex-shrink-0">
                {review.isBlind ? (
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => unblindMutation.mutate(review.reviewId)}
                    className="flex items-center gap-1"
                  >
                    <Eye className="h-3.5 w-3.5" />
                    해제
                  </Button>
                ) : (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => blindMutation.mutate(review.reviewId)}
                    className="flex items-center gap-1 text-red-500 hover:bg-red-50"
                  >
                    <EyeOff className="h-3.5 w-3.5" />
                    블라인드
                  </Button>
                )}
                {!review.reply && (
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setReplyModal({ open: true, reviewId: review.reviewId })}
                    className="flex items-center gap-1"
                  >
                    <MessageCircle className="h-3.5 w-3.5" />
                    답글
                  </Button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      <Pagination
        currentPage={response?.number ?? 0}
        totalPages={response?.totalPages ?? 1}
        onPageChange={setPage}
        className="mt-6"
      />

      {/* 답글 등록 모달 */}
      <Modal
        open={replyModal.open}
        onClose={() => setReplyModal({ open: false, reviewId: null })}
        title="리뷰 답글 등록"
        size="sm"
      >
        <div className="space-y-4">
          <textarea
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm h-28 resize-none focus:outline-none focus:ring-2 focus:ring-blue-200"
            placeholder="답글을 입력해주세요 (최대 1,000자)"
            maxLength={1000}
            value={replyContent}
            onChange={(e) => setReplyContent(e.target.value)}
          />
          <p className="text-xs text-right text-gray-400">{replyContent.length}/1000</p>
          <div className="flex gap-2">
            <Button variant="outline" className="flex-1" onClick={() => setReplyModal({ open: false, reviewId: null })}>
              취소
            </Button>
            <Button
              className="flex-1"
              loading={replyMutation.isPending}
              disabled={!replyContent.trim()}
              onClick={() => replyMutation.mutate()}
            >
              등록
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
