import Link from 'next/link';
import Image from 'next/image';
import { formatPrice } from '@/lib/utils';
import type { Product } from '@/types/product';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const mainImage = product.images?.find((img) => img.isMain) ?? product.images?.[0];
  const allSoldOut = product.options?.every((opt) => opt.stockQty === 0);

  return (
    <Link href={`/products/${product.productId}`} className="group block">
      <div className="relative overflow-hidden rounded-xl bg-gray-100 aspect-square">
        {mainImage ? (
          <Image
            src={mainImage.imageUrl}
            alt={product.productName}
            fill
            className="object-cover transition-transform duration-300 group-hover:scale-105"
            sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 25vw"
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center text-gray-400">
            <svg className="h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        )}
        {allSoldOut && (
          <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
            <span className="text-white font-semibold text-sm">품절</span>
          </div>
        )}
      </div>
      <div className="mt-3 space-y-1">
        <p className="text-sm text-gray-900 font-medium line-clamp-2 group-hover:text-blue-600 transition-colors">
          {product.productName}
        </p>
        <p className="text-base font-bold text-gray-900">{formatPrice(product.productPrice)}</p>
      </div>
    </Link>
  );
}
