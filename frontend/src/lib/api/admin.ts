import apiClient from './client';
import type { ApiResponse, PageResponse } from '@/types/api';
import type { Product } from '@/types/product';
import type { Order } from '@/types/order';
import type { Review } from './review';

export interface CreateProductRequest {
  shopId: number;
  categoryId: number;
  productName: string;
  productDescription?: string;
  productPrice: number;
  options?: { color?: string; size?: string; stockQty?: number; alertThreshold?: number }[];
  imageUrls?: string[];
}

export interface UpdateProductRequest {
  productName?: string;
  productDescription?: string;
  productPrice?: number;
  categoryId?: number;
}

export interface StockUpdateRequest {
  optionId: number;
  stockQty: number;
}

export const adminApi = {
  // Products
  getAllProducts: (page = 0, size = 20) =>
    apiClient.get<ApiResponse<PageResponse<Product>>>('/admin/products', {
      params: { page, size },
    }),

  createProduct: (data: CreateProductRequest) =>
    apiClient.post<ApiResponse<Product>>('/admin/products', data),

  updateProduct: (productId: number, data: UpdateProductRequest) =>
    apiClient.patch<ApiResponse<Product>>(`/admin/products/${productId}`, data),

  deleteProduct: (productId: number) =>
    apiClient.delete<ApiResponse<void>>(`/admin/products/${productId}`),

  toggleVisibility: (productId: number, visible: boolean) =>
    apiClient.patch<ApiResponse<void>>(
      `/admin/products/${productId}/visibility`,
      null,
      { params: { visible } }
    ),

  updateStock: (data: StockUpdateRequest) =>
    apiClient.patch<ApiResponse<void>>('/admin/inventory/stock', data),

  // Orders
  getAllOrders: (page = 0, size = 20) =>
    apiClient.get<ApiResponse<PageResponse<Order>>>('/admin/orders', {
      params: { page, size },
    }),

  changeOrderState: (orderId: number, state: string) =>
    apiClient.patch<ApiResponse<Order>>(`/admin/orders/${orderId}/state`, null, {
      params: { state },
    }),

  registerTracking: (orderId: number, carrier: string, trackingNumber: string) =>
    apiClient.post<ApiResponse<void>>(
      `/admin/orders/${orderId}/tracking`,
      null,
      { params: { carrier, trackingNumber } }
    ),

  getReturnRequests: (page = 0) =>
    apiClient.get<ApiResponse<PageResponse<any>>>('/admin/orders/returns', {
      params: { page },
    }),

  processReturn: (returnId: number, approve: boolean) =>
    apiClient.patch<ApiResponse<any>>(
      `/admin/orders/returns/${returnId}`,
      null,
      { params: { approve } }
    ),

  // Reviews
  getAllReviews: (page = 0, size = 20) =>
    apiClient.get<ApiResponse<PageResponse<Review>>>('/admin/reviews', {
      params: { page, size },
    }),

  blindReview: (reviewId: number) =>
    apiClient.patch<ApiResponse<Review>>(`/admin/reviews/${reviewId}/blind`),

  unblindReview: (reviewId: number) =>
    apiClient.patch<ApiResponse<Review>>(`/admin/reviews/${reviewId}/unblind`),

  addReply: (reviewId: number, content: string) =>
    apiClient.post<ApiResponse<Review>>(`/admin/reviews/${reviewId}/reply`, { content }),
};
