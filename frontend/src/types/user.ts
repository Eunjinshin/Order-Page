export interface User {
  userId: number
  nickname: string
  userAdmin: boolean
}

export interface UserProfile {
  profileId: number
  userId: number
  nickname: string | null
  userName: string | null
  userPhone: string | null
}

export interface UserAddress {
  addressId: number
  userId: number
  addressName: string | null
  address: string | null
  zipCode: string | null
  isDefault: boolean
  detailAddress: string | null
}

export type LoginType = 'EMAIL' | 'GOOGLE'
