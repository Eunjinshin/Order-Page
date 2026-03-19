import type { Metadata } from 'next'
import './globals.css'
import { QueryProvider } from '@/components/providers/QueryProvider'
import { AuthProvider } from '@/components/providers/AuthProvider'
import Header from '@/components/layout/Header'

export const metadata: Metadata = {
  title: {
    template: '%s | Odersite',
    default: 'Odersite — 주문 결제 플랫폼',
  },
  description: '상품 탐색부터 주문, 결제, 배송까지 한 번에',
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ko">
      <body>
        <AuthProvider>
          <QueryProvider>
            <Header />
            <main className="container-main py-6">{children}</main>
          </QueryProvider>
        </AuthProvider>
      </body>
    </html>
  )
}
