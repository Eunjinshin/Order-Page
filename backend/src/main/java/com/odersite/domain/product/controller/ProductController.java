package com.odersite.domain.product.controller;

import com.odersite.domain.product.dto.ProductDetailResponse;
import com.odersite.domain.product.dto.ProductSummaryResponse;
import com.odersite.domain.product.entity.Category;
import com.odersite.domain.product.repository.CategoryRepository;
import com.odersite.domain.product.service.ProductService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 API")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/products")
    @Operation(summary = "상품 목록 조회", description = "카테고리/키워드 필터, 페이지당 20개 (F-010, F-011)")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        return ResponseEntity.ok(ApiResponse.ok(productService.getProducts(categoryId, keyword, pageable)));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "상품 상세 조회", description = "가격, 이미지, 옵션별 재고 포함 (F-012, F-013)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProduct(productId)));
    }

    @GetMapping("/categories")
    @Operation(summary = "카테고리 목록 조회", description = "최상위 카테고리 목록 (depth=1)")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryRepository.findByParentIsNullOrderBySortOrderAsc()));
    }

    @GetMapping("/categories/{categoryId}/children")
    @Operation(summary = "하위 카테고리 조회", description = "특정 카테고리의 하위 카테고리 목록")
    public ResponseEntity<ApiResponse<List<Category>>> getChildCategories(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(
                categoryRepository.findByParent_CategoryIdOrderBySortOrderAsc(categoryId)));
    }
}
