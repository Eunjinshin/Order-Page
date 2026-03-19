import type { Metadata } from 'next';
import ProductDetail from '@/components/product/ProductDetail';

interface Props {
  params: { id: string };
}

export const metadata: Metadata = { title: '상품 상세 | Odersite' };

export default function ProductDetailPage({ params }: Props) {
  return <ProductDetail productId={Number(params.id)} />;
}
