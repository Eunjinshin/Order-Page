'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { authApi } from '@/lib/api/auth';

const schema = z.object({
  email: z.string().email('올바른 이메일을 입력해주세요.'),
  password: z
    .string()
    .min(8, '비밀번호는 8자 이상이어야 합니다.')
    .regex(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])/, '영문, 숫자, 특수문자를 모두 포함해야 합니다.'),
  passwordConfirm: z.string(),
  nickname: z.string().min(2, '닉네임은 2자 이상이어야 합니다.').max(20),
  userName: z.string().min(2, '이름은 2자 이상이어야 합니다.'),
  userPhone: z
    .string()
    .regex(/^01[0-9]-?\d{3,4}-?\d{4}$/, '올바른 휴대폰 번호를 입력해주세요.'),
}).refine((data) => data.password === data.passwordConfirm, {
  message: '비밀번호가 일치하지 않습니다.',
  path: ['passwordConfirm'],
});

type FormValues = z.infer<typeof schema>;

export default function SignupForm() {
  const router = useRouter();
  const [serverError, setServerError] = useState('');
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = async ({ passwordConfirm: _, ...data }: FormValues) => {
    setServerError('');
    try {
      await authApi.signup(data);
      setSuccess(true);
      setTimeout(() => router.push('/login'), 2000);
    } catch (err: any) {
      setServerError(err.response?.data?.message || '회원가입에 실패했습니다.');
    }
  };

  if (success) {
    return (
      <div className="text-center py-8 space-y-3">
        <div className="text-5xl">🎉</div>
        <p className="text-lg font-semibold text-gray-900">회원가입이 완료되었습니다!</p>
        <p className="text-sm text-gray-500">잠시 후 로그인 페이지로 이동합니다.</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input
        label="이메일"
        type="email"
        placeholder="example@email.com"
        error={errors.email?.message}
        {...register('email')}
      />
      <Input
        label="비밀번호"
        type="password"
        placeholder="영문+숫자+특수문자 8자 이상"
        error={errors.password?.message}
        hint="영문, 숫자, 특수문자(!@#$%^&*)를 모두 포함해야 합니다."
        {...register('password')}
      />
      <Input
        label="비밀번호 확인"
        type="password"
        placeholder="비밀번호를 다시 입력하세요"
        error={errors.passwordConfirm?.message}
        {...register('passwordConfirm')}
      />
      <Input
        label="닉네임"
        placeholder="2~20자"
        error={errors.nickname?.message}
        {...register('nickname')}
      />
      <Input
        label="이름"
        placeholder="홍길동"
        error={errors.userName?.message}
        {...register('userName')}
      />
      <Input
        label="휴대폰 번호"
        type="tel"
        placeholder="010-1234-5678"
        error={errors.userPhone?.message}
        {...register('userPhone')}
      />

      {serverError && (
        <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
          {serverError}
        </p>
      )}

      <Button type="submit" size="lg" loading={isSubmitting} className="w-full mt-2">
        회원가입
      </Button>

      <p className="text-center text-sm text-gray-600">
        이미 계정이 있으신가요?{' '}
        <Link href="/login" className="text-blue-600 hover:underline font-medium">
          로그인
        </Link>
      </p>
    </form>
  );
}
