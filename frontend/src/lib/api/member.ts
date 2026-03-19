import apiClient from './client';
import type { ApiResponse } from '@/types/api';
import type { UserProfile, UserAddress } from '@/types/user';

export interface UpdateProfileRequest {
  nickname?: string;
  userName?: string;
  userPhone?: string;
}

export interface AddressRequest {
  addressName: string;
  address: string;
  detailAddress?: string;
  zipCode: string;
  isDefault?: boolean;
}

export const memberApi = {
  getProfile: () =>
    apiClient.get<ApiResponse<UserProfile>>('/me/profile'),

  updateProfile: (data: UpdateProfileRequest) =>
    apiClient.patch<ApiResponse<UserProfile>>('/me/profile', data),

  getAddresses: () =>
    apiClient.get<ApiResponse<UserAddress[]>>('/me/addresses'),

  addAddress: (data: AddressRequest) =>
    apiClient.post<ApiResponse<UserAddress>>('/me/addresses', data),

  updateAddress: (addressId: number, data: AddressRequest) =>
    apiClient.put<ApiResponse<UserAddress>>(`/me/addresses/${addressId}`, data),

  deleteAddress: (addressId: number) =>
    apiClient.delete<ApiResponse<void>>(`/me/addresses/${addressId}`),

  setDefaultAddress: (addressId: number) =>
    apiClient.patch<ApiResponse<void>>(`/me/addresses/${addressId}/default`),
};
