'use client'

import Link from 'next/link'
import { ShoppingCart, User, Search } from 'lucide-react'
import { useSession } from 'next-auth/react'

export default function Header() {
  const { data: session } = useSession()

  return (
    <header className="sticky top-0 z-50 bg-white border-b border-gray-200 shadow-sm">
      <div className="container-main flex items-center justify-between h-16">
        {/* 로고 */}
        <Link href="/" className="text-2xl font-bold text-primary-600">
          Odersite
        </Link>

        {/* 검색 */}
        <div className="flex-1 max-w-md mx-8">
          <div className="relative">
            <input
              type="text"
              placeholder="상품 검색"
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg
                         focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          </div>
        </div>

        {/* 우측 메뉴 */}
        <nav className="flex items-center gap-4">
          <Link href="/cart" className="relative p-2 hover:text-primary-600 transition-colors">
            <ShoppingCart className="h-6 w-6" />
          </Link>

          {session ? (
            <Link href="/mypage" className="flex items-center gap-1 hover:text-primary-600 transition-colors">
              <User className="h-6 w-6" />
              <span className="text-sm hidden sm:block">{session.user?.name}</span>
            </Link>
          ) : (
            <Link
              href="/login"
              className="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm font-medium
                         hover:bg-primary-700 transition-colors"
            >
              로그인
            </Link>
          )}
        </nav>
      </div>
    </header>
  )
}
