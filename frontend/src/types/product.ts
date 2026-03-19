export interface Product {
  productId: number
  shopId: number
  categoryId: number
  productName: string
  productDescription: string | null
  productPrice: number
  isVisible: boolean
  createdAt: string
  images: ProductImage[]
  options: ProductOption[]
}

export interface ProductOption {
  optionId: number
  productId: number
  color: string | null
  size: string | null
  stockQty: number
  alertThreshold: number | null
}

export interface ProductImage {
  imageId: number
  productId: number
  imageUrl: string | null
  isMain: boolean
  sortOrder: number | null
}

export interface Category {
  categoryId: number
  parentId: number | null
  categoryName: string | null
  depth: number | null
  sortOrder: number
  children?: Category[]
}
