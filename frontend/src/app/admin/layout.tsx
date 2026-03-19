import Link from 'next/link';
import { Package, ShoppingBag, MessageSquare, LayoutDashboard } from 'lucide-react';

const navItems = [
  { href: '/admin', label: '대시보드', icon: LayoutDashboard },
  { href: '/admin/products', label: '상품 관리', icon: Package },
  { href: '/admin/orders', label: '주문 관리', icon: ShoppingBag },
  { href: '/admin/reviews', label: '리뷰 관리', icon: MessageSquare },
];

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* 사이드바 */}
      <aside className="w-56 bg-white border-r border-gray-200 flex-shrink-0">
        <div className="p-4 border-b border-gray-200">
          <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Admin</p>
          <p className="font-bold text-gray-900 mt-0.5">Odersite 관리자</p>
        </div>
        <nav className="p-3 space-y-1">
          {navItems.map(({ href, label, icon: Icon }) => (
            <Link
              key={href}
              href={href}
              className="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-700 transition-colors"
            >
              <Icon className="h-4 w-4" />
              {label}
            </Link>
          ))}
        </nav>
      </aside>

      {/* 메인 컨텐츠 */}
      <main className="flex-1 overflow-auto">{children}</main>
    </div>
  );
}
