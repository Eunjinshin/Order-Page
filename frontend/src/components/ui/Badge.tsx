import { cn } from '@/lib/utils';

type BadgeVariant = 'default' | 'success' | 'warning' | 'danger' | 'info' | 'gray';

const variantStyles: Record<BadgeVariant, string> = {
  default: 'bg-blue-100 text-blue-800',
  success: 'bg-green-100 text-green-800',
  warning: 'bg-yellow-100 text-yellow-800',
  danger: 'bg-red-100 text-red-800',
  info: 'bg-purple-100 text-purple-800',
  gray: 'bg-gray-100 text-gray-700',
};

interface BadgeProps {
  children: React.ReactNode;
  variant?: BadgeVariant;
  className?: string;
}

export default function Badge({ children, variant = 'default', className }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
        variantStyles[variant],
        className
      )}
    >
      {children}
    </span>
  );
}

export function orderStateBadge(state: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    PENDING: { label: '결제 대기', variant: 'warning' },
    PAID: { label: '결제 완료', variant: 'success' },
    PREPARING: { label: '상품 준비중', variant: 'info' },
    SHIPPED: { label: '배송중', variant: 'default' },
    DELIVERED: { label: '배송 완료', variant: 'success' },
    CANCELLED: { label: '취소됨', variant: 'danger' },
  };
  return map[state] ?? { label: state, variant: 'gray' as BadgeVariant };
}

export function deliveryStateBadge(state: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    PREPARING: { label: '배송 준비', variant: 'warning' },
    SHIPPED: { label: '발송 완료', variant: 'info' },
    IN_TRANSIT: { label: '배송중', variant: 'default' },
    DELIVERED: { label: '배송 완료', variant: 'success' },
  };
  return map[state] ?? { label: state, variant: 'gray' as BadgeVariant };
}
