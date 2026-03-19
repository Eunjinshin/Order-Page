import apiClient from './client';
import type { ApiResponse, PageResponse } from '@/types/api';
import type { Order } from '@/types/order';

export interface OrderItemRequest {
  optionId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  addressId: number;
  couponId?: number;
  items: OrderItemRequest[];
}

export interface ReturnRequest {
  type: 'RETURN' | 'EXCHANGE';
  reason: string;
}

export const orderApi = {
  createOrder: (data: CreateOrderRequest) =>
    apiClient.post<ApiResponse<Order>>('/orders', data),

  getMyOrders: (page = 0, size = 10) =>
    apiClient.get<ApiResponse<PageResponse<Order>>>('/orders', {
      params: { page, size },
    }),

  getOrder: (orderId: number) =>
    apiClient.get<ApiResponse<Order>>(`/orders/${orderId}`),

  cancelOrder: (orderId: number) =>
    apiClient.patch<ApiResponse<Order>>(`/orders/${orderId}/cancel`),

  requestReturn: (orderItemId: number, data: ReturnRequest) =>
    apiClient.post<ApiResponse<void>>(`/orders/items/${orderItemId}/returns`, data),
};
