import apiClient from './client';
import type { ApiResponse, PageResponse } from '@/types/api';

export interface Review {
  reviewId: number;
  userId: number;
  productId: number;
  orderItemId: number;
  rating: number;
  content: string;
  isBlind: boolean;
  createdAt: string;
  images: { imageId: number; imageUrl: string; sortOrder: number }[];
  helpfulCount: number;
  reply?: { replyId: number; content: string; createdAt: string };
}

export interface CreateReviewRequest {
  productId: number;
  orderItemId: number;
  rating: number;
  content: string;
  imageUrls?: string[];
}

export const reviewApi = {
  getProductReviews: (productId: number, page = 0, size = 10) =>
    apiClient.get<ApiResponse<PageResponse<Review>>>(
      `/products/${productId}/reviews`,
      { params: { page, size } }
    ),

  createReview: (data: CreateReviewRequest) =>
    apiClient.post<ApiResponse<Review>>('/reviews', data),

  updateReview: (reviewId: number, content: string, rating: number) =>
    apiClient.patch<ApiResponse<Review>>(`/reviews/${reviewId}`, null, {
      params: { content, rating },
    }),

  deleteReview: (reviewId: number) =>
    apiClient.delete<ApiResponse<void>>(`/reviews/${reviewId}`),

  markHelpful: (reviewId: number) =>
    apiClient.post<ApiResponse<void>>(`/reviews/${reviewId}/helpful`),
};
