'use client';

import Link from 'next/link';
import { Package, ShoppingBag, MessageSquare, BarChart3, Users } from 'lucide-react';

const menuItems = [
  {
    href: '/admin/products',
    icon: Package,
    label: '상품 관리',
    desc: '상품 등록·수정·삭제, 재고 관리',
    color: 'bg-blue-100 text-blue-600',
  },
  {
    href: '/admin/orders',
    icon: ShoppingBag,
    label: '주문 관리',
    desc: '주문 상태 변경, 송장 등록, 반품 처리',
    color: 'bg-green-100 text-green-600',
  },
  {
    href: '/admin/reviews',
    icon: MessageSquare,
    label: '리뷰 관리',
    desc: '블라인드 처리, 답글 등록',
    color: 'bg-purple-100 text-purple-600',
  },
];

export default function AdminPage() {
  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-8">
      <div className="flex items-center gap-3">
        <BarChart3 className="h-7 w-7 text-blue-600" />
        <h1 className="text-2xl font-bold text-gray-900">관리자 대시보드</h1>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {menuItems.map(({ href, icon: Icon, label, desc, color }) => (
          <Link
            key={href}
            href={href}
            className="flex flex-col items-start gap-3 p-6 border border-gray-200 rounded-2xl hover:border-blue-300 hover:shadow-sm transition-all group"
          >
            <div className={`p-3 rounded-xl ${color}`}>
              <Icon className="h-6 w-6" />
            </div>
            <div>
              <p className="font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
                {label}
              </p>
              <p className="text-sm text-gray-500 mt-0.5">{desc}</p>
            </div>
          </Link>
        ))}
      </div>

      <div className="border border-yellow-200 bg-yellow-50 rounded-xl p-4 text-sm text-yellow-800">
        <strong>참고:</strong> 관리자 페이지는 <code>isAdmin=true</code> 계정으로만 접근 가능합니다.
        프로덕션 환경에서는 서버 사이드 인증 미들웨어로 보호합니다.
      </div>
    </div>
  );
}
