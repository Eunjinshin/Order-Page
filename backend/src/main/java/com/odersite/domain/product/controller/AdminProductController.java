package com.odersite.domain.product.controller;

import com.odersite.domain.product.dto.*;
import com.odersite.domain.product.service.AdminProductService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin-Product", description = "관리자 상품 API")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "전체 상품 목록", description = "관리자용 전체 상품 목록 조회 (A-010)")
    public ResponseEntity<ApiResponse<Page<ProductDetailResponse>>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(adminProductService.getAllProducts(pageable)));
    }

    @PostMapping
    @Operation(summary = "상품 등록", description = "새 상품 + 옵션 + 이미지 등록 (A-011)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminProductService.createProduct(request)));
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품 기본 정보 수정 (A-012)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable Integer productId,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminProductService.updateProduct(productId, request)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품 삭제 (A-013)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{productId}/visibility")
    @Operation(summary = "상품 노출 여부 변경", description = "상품 공개/비공개 전환 (A-014)")
    public ResponseEntity<ApiResponse<Void>> toggleVisibility(
            @PathVariable Integer productId,
            @RequestParam boolean visible) {
        adminProductService.toggleVisibility(productId, visible);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
