'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { User, MapPin, ShoppingBag, LogOut } from 'lucide-react';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { PageLoader } from '@/components/ui/Spinner';
import { memberApi } from '@/lib/api/member';
import { authApi } from '@/lib/api/auth';
import { useAuthStore } from '@/store/authStore';

const schema = z.object({
  nickname: z.string().min(2, '2자 이상 입력해주세요.').optional(),
  userName: z.string().min(2, '2자 이상 입력해주세요.').optional(),
  userPhone: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

export default function MyPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { logout } = useAuthStore();

  const { data, isLoading } = useQuery({
    queryKey: ['myProfile'],
    queryFn: () => memberApi.getProfile(),
  });

  const profile = data?.data?.data;

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting, isDirty },
    reset,
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    values: {
      nickname: profile?.nickname ?? '',
      userName: profile?.userName ?? '',
      userPhone: profile?.userPhone ?? '',
    },
  });

  const updateMutation = useMutation({
    mutationFn: (values: FormValues) => memberApi.updateProfile(values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['myProfile'] });
    },
  });

  const logoutMutation = useMutation({
    mutationFn: () => authApi.logout(),
    onSettled: () => {
      logout();
      router.push('/login');
    },
  });

  if (isLoading) return <PageLoader />;

  return (
    <div className="max-w-2xl mx-auto px-4 py-8 space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
        <User className="h-6 w-6" />
        마이페이지
      </h1>

      {/* 빠른 메뉴 */}
      <div className="grid grid-cols-2 gap-3">
        <button
          onClick={() => router.push('/orders')}
          className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-blue-300 hover:bg-blue-50 transition-colors text-left"
        >
          <ShoppingBag className="h-5 w-5 text-blue-600" />
          <div>
            <p className="font-medium text-gray-900">주문 내역</p>
            <p className="text-xs text-gray-500">주문 조회 및 반품</p>
          </div>
        </button>
        <button
          onClick={() => router.push('/mypage/addresses')}
          className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-blue-300 hover:bg-blue-50 transition-colors text-left"
        >
          <MapPin className="h-5 w-5 text-blue-600" />
          <div>
            <p className="font-medium text-gray-900">배송지 관리</p>
            <p className="text-xs text-gray-500">배송지 추가 및 수정</p>
          </div>
        </button>
      </div>

      {/* 프로필 정보 수정 */}
      <div className="border border-gray-200 rounded-xl p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-5">프로필 정보</h2>
        <form
          onSubmit={handleSubmit((values) => updateMutation.mutate(values))}
          className="space-y-4"
        >
          <Input
            label="닉네임"
            error={errors.nickname?.message}
            {...register('nickname')}
          />
          <Input
            label="이름"
            error={errors.userName?.message}
            {...register('userName')}
          />
          <Input
            label="휴대폰 번호"
            type="tel"
            error={errors.userPhone?.message}
            {...register('userPhone')}
          />

          {updateMutation.isSuccess && (
            <p className="text-sm text-green-600">프로필이 수정되었습니다.</p>
          )}
          {updateMutation.isError && (
            <p className="text-sm text-red-600">수정에 실패했습니다.</p>
          )}

          <Button
            type="submit"
            loading={isSubmitting || updateMutation.isPending}
            disabled={!isDirty}
          >
            저장
          </Button>
        </form>
      </div>

      {/* 로그아웃 */}
      <div className="border border-red-100 rounded-xl p-6">
        <h2 className="text-base font-semibold text-gray-900 mb-3">계정</h2>
        <Button
          variant="danger"
          loading={logoutMutation.isPending}
          onClick={() => logoutMutation.mutate()}
          className="flex items-center gap-2"
        >
          <LogOut className="h-4 w-4" />
          로그아웃
        </Button>
      </div>
    </div>
  );
}
