import type { Metadata } from 'next';
import SignupForm from '@/components/auth/SignupForm';

export const metadata: Metadata = { title: '회원가입 | Odersite' };

export default function SignupPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold text-gray-900">회원가입</h1>
            <p className="text-sm text-gray-500 mt-1">새 계정을 만들어보세요</p>
          </div>
          <SignupForm />
        </div>
      </div>
    </div>
  );
}
