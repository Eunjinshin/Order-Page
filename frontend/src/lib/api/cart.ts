import apiClient from './client';
import type { ApiResponse } from '@/types/api';
import type { CartItem } from '@/types/order';

export interface AddCartRequest {
  optionId: number;
  quantity: number;
}

export const cartApi = {
  getCart: () =>
    apiClient.get<ApiResponse<CartItem[]>>('/cart'),

  addToCart: (data: AddCartRequest) =>
    apiClient.post<ApiResponse<CartItem>>('/cart', data),

  updateCartItem: (cartId: number, quantity: number) =>
    apiClient.patch<ApiResponse<CartItem>>(`/cart/${cartId}`, { quantity }),

  deleteCartItem: (cartId: number) =>
    apiClient.delete<ApiResponse<void>>(`/cart/${cartId}`),

  clearCart: () =>
    apiClient.delete<ApiResponse<void>>('/cart'),
};
