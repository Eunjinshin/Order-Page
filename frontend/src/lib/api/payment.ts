import apiClient from './client';
import type { ApiResponse } from '@/types/api';
import type { Payment } from '@/types/order';

export interface PaymentRequest {
  orderId: number;
  paymentMethod: 'CARD' | 'KAKAO' | 'NAVER' | 'TRANSFER' | 'DEPOSIT';
  pgProvider: 'TOSSPAYMENTS' | 'KGINIIS';
}

export const paymentApi = {
  preparePayment: (data: PaymentRequest) =>
    apiClient.post<ApiResponse<Payment>>('/payments/prepare', data),

  confirmPayment: (paymentId: number, pgTransactionId?: string) =>
    apiClient.post<ApiResponse<Payment>>(`/payments/${paymentId}/confirm`, null, {
      params: pgTransactionId ? { pgTransactionId } : {},
    }),

  getPayment: (orderId: number) =>
    apiClient.get<ApiResponse<Payment>>(`/payments/orders/${orderId}`),
};
