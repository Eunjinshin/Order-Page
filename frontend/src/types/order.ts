export type OrderState = 'PENDING' | 'PAID' | 'PREPARING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
export type ReturnType = 'RETURN' | 'EXCHANGE'
export type ReturnStatus = 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'COMPLETED'
export type PaymentMethod = 'CARD' | 'KAKAO' | 'NAVER' | 'TRANSFER' | 'DEPOSIT'
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'CANCELLED'
export type DeliveryStatus = 'PREPARING' | 'SHIPPED' | 'IN_TRANSIT' | 'DELIVERED'

export interface CartItem {
  cartId: number
  userId: number
  optionId: number
  quantity: number
  createdAt: string
  productName: string
  productPrice: number
  color: string | null
  size: string | null
  imageUrl: string | null
}

export interface Order {
  orderId: number
  userId: number
  addressId: number
  couponId: number | null
  totalPrice: number | null
  discountPrice: number | null
  finalPrice: number
  orderState: OrderState
  orderedAt: string
  items: OrderItem[]
}

export interface OrderItem {
  orderItemId: number
  orderId: number
  optionId: number
  quantity: number | null
  unitPrice: number | null
}

export interface Payment {
  paymentId: number
  orderId: number
  pgProvider: 'TOSSPAYMENTS' | 'KGINIIS' | null
  pgTransactionId: string | null
  paymentMethod: PaymentMethod
  amount: number
  status: PaymentStatus
  paidAt: string | null
}
