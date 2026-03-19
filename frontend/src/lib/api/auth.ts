import apiClient from './client';
import type { ApiResponse } from '@/types/api';

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
  userName: string;
  userPhone: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  nickname: string;
  isAdmin: boolean;
}

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordResetConfirmRequest {
  email: string;
  token: string;
  newPassword: string;
}

export const authApi = {
  signup: (data: SignupRequest) =>
    apiClient.post<ApiResponse<void>>('/auth/signup', data),

  login: (data: LoginRequest) =>
    apiClient.post<ApiResponse<TokenResponse>>('/auth/login', data),

  refresh: (refreshToken: string) =>
    apiClient.post<ApiResponse<TokenResponse>>('/auth/refresh', { refreshToken }),

  logout: () =>
    apiClient.post<ApiResponse<void>>('/auth/logout'),

  withdraw: () =>
    apiClient.delete<ApiResponse<void>>('/auth/withdraw'),

  requestPasswordReset: (data: PasswordResetRequest) =>
    apiClient.post<ApiResponse<void>>('/auth/password-reset/request', data),

  confirmPasswordReset: (data: PasswordResetConfirmRequest) =>
    apiClient.post<ApiResponse<void>>('/auth/password-reset/confirm', data),
};
