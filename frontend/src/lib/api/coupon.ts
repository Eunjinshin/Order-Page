import apiClient from './client';
import type { ApiResponse } from '@/types/api';

export interface UserCoupon {
  userCouponId: number;
  couponId: number;
  name: string;
  discountType: 'RATE' | 'FIXED';
  discountValue: number;
  minOrderAmount: number;
  validFrom: string;
  validUntil: string;
  isUsed: boolean;
}

export const couponApi = {
  getMyCoupons: () =>
    apiClient.get<ApiResponse<UserCoupon[]>>('/coupons/me'),
};
