package com.odersite.domain.product.controller;

import com.odersite.domain.product.dto.CategoryRequest;
import com.odersite.domain.product.entity.Category;
import com.odersite.domain.product.repository.CategoryRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin-Category", description = "관리자 카테고리 API")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    @Operation(summary = "카테고리 생성", description = "신규 카테고리 등록 (A-015)")
    public ResponseEntity<ApiResponse<Void>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        }
        categoryRepository.save(Category.builder()
                .categoryName(request.getCategoryName())
                .parent(parent)
                .depth(request.getDepth() != null ? request.getDepth() : (parent != null ? 2 : 1))
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "카테고리 이름 및 정렬 순서 수정 (A-016)")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        category.update(
                request.getCategoryName() != null ? request.getCategoryName() : category.getCategoryName(),
                request.getSortOrder() != null ? request.getSortOrder() : category.getSortOrder()
        );
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제 (A-017) — 상품이 없는 경우만 허용")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        categoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
