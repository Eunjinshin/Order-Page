import apiClient from './client';
import type { ApiResponse, PageResponse } from '@/types/api';
import type { Category, Product } from '@/types/product';

export interface ProductFilterParams {
  categoryId?: number;
  keyword?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export const productApi = {
  getProducts: (params?: ProductFilterParams) =>
    apiClient.get<ApiResponse<PageResponse<Product>>>('/products', { params }),

  getProduct: (productId: number) =>
    apiClient.get<ApiResponse<Product>>(`/products/${productId}`),

  getCategories: () =>
    apiClient.get<ApiResponse<Category[]>>('/categories'),

  getCategoryChildren: (categoryId: number) =>
    apiClient.get<ApiResponse<Category[]>>(`/categories/${categoryId}/children`),
};
