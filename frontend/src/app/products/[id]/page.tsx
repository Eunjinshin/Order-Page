interface ProductDetailPageProps {
  params: { id: string }
}

export default function ProductDetailPage({ params }: ProductDetailPageProps) {
  return (
    <div>
      <h1 className="text-2xl font-bold">상품 상세</h1>
      <p className="text-gray-500">상품 ID: {params.id}</p>
      {/* TODO: ProductDetail 컴포넌트 */}
    </div>
  )
}
