package com.odersite.domain.product.controller;

import com.odersite.domain.product.dto.StockUpdateRequest;
import com.odersite.domain.product.service.AdminProductService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@Tag(name = "Admin-Inventory", description = "관리자 재고 API")
public class AdminInventoryController {

    private final AdminProductService adminProductService;

    @PatchMapping("/stock")
    @Operation(summary = "재고 수량 직접 수정", description = "옵션별 재고 수량 일괄 수정 (A-030)")
    public ResponseEntity<ApiResponse<Void>> updateStock(@Valid @RequestBody StockUpdateRequest request) {
        adminProductService.updateStock(request.getOptionId(), request.getStockQty());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
