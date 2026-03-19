export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T | null
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface ErrorResponse {
  success: false
  message: string
  data: null
}
