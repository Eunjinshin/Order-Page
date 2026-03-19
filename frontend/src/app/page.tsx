'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import ProductCard from '@/components/product/ProductCard';
import CategoryFilter from '@/components/product/CategoryFilter';
import Pagination from '@/components/ui/Pagination';
import { PageLoader } from '@/components/ui/Spinner';
import { productApi } from '@/lib/api/product';

export default function HomePage() {
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [keyword, setKeyword] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [page, setPage] = useState(0);

  const { data: categoriesRes } = useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });

  const { data: productsRes, isLoading } = useQuery({
    queryKey: ['products', selectedCategory, keyword, page],
    queryFn: () =>
      productApi.getProducts({
        categoryId: selectedCategory ?? undefined,
        keyword: keyword || undefined,
        page,
        size: 20,
      }),
  });

  const categories = categoriesRes?.data?.data ?? [];
  const products = productsRes?.data?.data;

  const handleSearch = () => {
    setKeyword(inputValue);
    setPage(0);
  };

  const handleCategoryChange = (id: number | null) => {
    setSelectedCategory(id);
    setPage(0);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-6">
      {/* 검색 바 */}
      <div className="flex gap-2">
        <div className="relative flex-1 max-w-xl">
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            placeholder="상품을 검색해보세요"
            className="w-full pl-4 pr-10 py-2.5 border border-gray-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-200 focus:border-blue-500"
          />
          <button
            onClick={handleSearch}
            className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-blue-600"
          >
            <Search className="h-5 w-5" />
          </button>
        </div>
      </div>

      {/* 카테고리 필터 */}
      {categories.length > 0 && (
        <CategoryFilter
          categories={categories}
          selectedId={selectedCategory}
          onSelect={handleCategoryChange}
        />
      )}

      {/* 키워드 표시 */}
      {keyword && (
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <span>
            &quot;<strong>{keyword}</strong>&quot; 검색 결과
          </span>
          <button
            onClick={() => { setKeyword(''); setInputValue(''); setPage(0); }}
            className="text-blue-600 hover:underline"
          >
            초기화
          </button>
        </div>
      )}

      {/* 상품 목록 */}
      {isLoading ? (
        <PageLoader />
      ) : (products?.content?.length ?? 0) === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-lg">상품이 없습니다.</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
            {products!.content.map((product) => (
              <ProductCard key={product.productId} product={product} />
            ))}
          </div>
          <Pagination
            currentPage={products!.number}
            totalPages={products!.totalPages}
            onPageChange={setPage}
          />
        </>
      )}
    </div>
  );
}
