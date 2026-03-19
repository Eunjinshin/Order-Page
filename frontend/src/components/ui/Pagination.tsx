'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';
import { cn } from '@/lib/utils';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  className?: string;
}

export default function Pagination({ currentPage, totalPages, onPageChange, className }: PaginationProps) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i);
  const start = Math.max(0, currentPage - 2);
  const end = Math.min(totalPages, start + 5);
  const visible = pages.slice(start, end);

  return (
    <nav className={cn('flex items-center justify-center gap-1', className)}>
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <ChevronLeft className="h-4 w-4" />
      </button>

      {start > 0 && (
        <>
          <PageBtn page={0} current={currentPage} onClick={onPageChange} />
          {start > 1 && <span className="px-2 text-gray-400">…</span>}
        </>
      )}

      {visible.map((p) => (
        <PageBtn key={p} page={p} current={currentPage} onClick={onPageChange} />
      ))}

      {end < totalPages && (
        <>
          {end < totalPages - 1 && <span className="px-2 text-gray-400">…</span>}
          <PageBtn page={totalPages - 1} current={currentPage} onClick={onPageChange} />
        </>
      )}

      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <ChevronRight className="h-4 w-4" />
      </button>
    </nav>
  );
}

function PageBtn({ page, current, onClick }: { page: number; current: number; onClick: (p: number) => void }) {
  return (
    <button
      onClick={() => onClick(page)}
      className={cn(
        'w-9 h-9 rounded-lg text-sm font-medium transition-colors',
        page === current
          ? 'bg-blue-600 text-white'
          : 'text-gray-700 hover:bg-gray-100'
      )}
    >
      {page + 1}
    </button>
  );
}
