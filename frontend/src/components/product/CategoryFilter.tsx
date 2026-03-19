'use client';

import { cn } from '@/lib/utils';
import type { Category } from '@/types/product';

interface CategoryFilterProps {
  categories: Category[];
  selectedId: number | null;
  onSelect: (id: number | null) => void;
}

export default function CategoryFilter({ categories, selectedId, onSelect }: CategoryFilterProps) {
  return (
    <div className="flex flex-wrap gap-2">
      <button
        onClick={() => onSelect(null)}
        className={cn(
          'px-4 py-2 rounded-full text-sm font-medium transition-colors border',
          selectedId === null
            ? 'bg-blue-600 text-white border-blue-600'
            : 'bg-white text-gray-700 border-gray-300 hover:border-blue-400 hover:text-blue-600'
        )}
      >
        전체
      </button>
      {categories.map((cat) => (
        <button
          key={cat.categoryId}
          onClick={() => onSelect(cat.categoryId)}
          className={cn(
            'px-4 py-2 rounded-full text-sm font-medium transition-colors border',
            selectedId === cat.categoryId
              ? 'bg-blue-600 text-white border-blue-600'
              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-400 hover:text-blue-600'
          )}
        >
          {cat.categoryName}
        </button>
      ))}
    </div>
  );
}
