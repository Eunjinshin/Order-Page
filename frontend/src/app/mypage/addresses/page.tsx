'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Pencil, Trash2, MapPin } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';
import Modal from '@/components/ui/Modal';
import { PageLoader } from '@/components/ui/Spinner';
import { memberApi } from '@/lib/api/member';
import type { UserAddress } from '@/types/user';

const schema = z.object({
  addressName: z.string().min(1, '주소 별칭을 입력해주세요.'),
  zipCode: z.string().min(5, '우편번호를 입력해주세요.'),
  address: z.string().min(5, '주소를 입력해주세요.'),
  detailAddress: z.string().optional(),
  isDefault: z.boolean().optional(),
});

type FormValues = z.infer<typeof schema>;

export default function AddressesPage() {
  const queryClient = useQueryClient();
  const [modal, setModal] = useState<{ open: boolean; editing: UserAddress | null }>({
    open: false,
    editing: null,
  });

  const { data, isLoading } = useQuery({
    queryKey: ['addresses'],
    queryFn: () => memberApi.getAddresses(),
  });

  const addresses: UserAddress[] = data?.data?.data ?? [];

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const openAdd = () => {
    reset({ addressName: '', zipCode: '', address: '', detailAddress: '', isDefault: false });
    setModal({ open: true, editing: null });
  };

  const openEdit = (addr: UserAddress) => {
    reset({
      addressName: addr.addressName,
      zipCode: addr.zipCode,
      address: addr.address,
      detailAddress: addr.detailAddress ?? '',
      isDefault: addr.isDefault,
    });
    setModal({ open: true, editing: addr });
  };

  const saveMutation = useMutation({
    mutationFn: (values: FormValues) =>
      modal.editing
        ? memberApi.updateAddress(modal.editing.addressId, values)
        : memberApi.addAddress(values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      setModal({ open: false, editing: null });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => memberApi.deleteAddress(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['addresses'] }),
  });

  const setDefaultMutation = useMutation({
    mutationFn: (id: number) => memberApi.setDefaultAddress(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['addresses'] }),
  });

  if (isLoading) return <PageLoader />;

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <MapPin className="h-6 w-6" />
          배송지 관리
        </h1>
        <Button size="sm" onClick={openAdd} className="flex items-center gap-1">
          <Plus className="h-4 w-4" />
          배송지 추가
        </Button>
      </div>

      {addresses.length === 0 ? (
        <div className="text-center py-20 text-gray-400 space-y-3">
          <MapPin className="h-14 w-14 mx-auto text-gray-200" />
          <p>등록된 배송지가 없습니다.</p>
          <Button onClick={openAdd}>배송지 추가</Button>
        </div>
      ) : (
        <div className="space-y-3">
          {addresses.map((addr) => (
            <div
              key={addr.addressId}
              className={`border rounded-xl p-5 ${
                addr.isDefault ? 'border-blue-400 bg-blue-50' : 'border-gray-200'
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex-1 text-sm space-y-1">
                  <div className="flex items-center gap-2">
                    <p className="font-semibold text-gray-900">{addr.addressName}</p>
                    {addr.isDefault && (
                      <span className="text-xs bg-blue-600 text-white px-2 py-0.5 rounded-full">기본</span>
                    )}
                  </div>
                  <p className="text-gray-600">({addr.zipCode}) {addr.address}</p>
                  {addr.detailAddress && <p className="text-gray-600">{addr.detailAddress}</p>}
                </div>
                <div className="flex gap-1 flex-shrink-0">
                  <button
                    onClick={() => openEdit(addr)}
                    className="p-1.5 text-gray-400 hover:text-blue-500 transition-colors"
                  >
                    <Pencil className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => deleteMutation.mutate(addr.addressId)}
                    className="p-1.5 text-gray-400 hover:text-red-500 transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              </div>
              {!addr.isDefault && (
                <button
                  onClick={() => setDefaultMutation.mutate(addr.addressId)}
                  className="mt-2 text-xs text-blue-600 hover:underline"
                >
                  기본 배송지로 설정
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {/* 추가/수정 모달 */}
      <Modal
        open={modal.open}
        onClose={() => setModal({ open: false, editing: null })}
        title={modal.editing ? '배송지 수정' : '배송지 추가'}
      >
        <form onSubmit={handleSubmit((v) => saveMutation.mutate(v))} className="space-y-4">
          <Input label="주소 별칭" placeholder="집, 회사 등" error={errors.addressName?.message} {...register('addressName')} />
          <Input label="우편번호" placeholder="12345" error={errors.zipCode?.message} {...register('zipCode')} />
          <Input label="주소" placeholder="도로명 주소" error={errors.address?.message} {...register('address')} />
          <Input label="상세 주소" placeholder="101호 (선택)" error={errors.detailAddress?.message} {...register('detailAddress')} />
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input type="checkbox" {...register('isDefault')} className="rounded" />
            기본 배송지로 설정
          </label>
          {saveMutation.isError && (
            <p className="text-xs text-red-600">저장에 실패했습니다.</p>
          )}
          <div className="flex gap-2 pt-2">
            <Button variant="outline" className="flex-1" onClick={() => setModal({ open: false, editing: null })}>
              취소
            </Button>
            <Button type="submit" className="flex-1" loading={isSubmitting || saveMutation.isPending}>
              저장
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
