package com.odersite.domain.order.controller;

import com.odersite.domain.order.dto.OrderResponse;
import com.odersite.domain.order.dto.ReturnRequestResponse;
import com.odersite.domain.order.service.AdminOrderService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin-Order", description = "관리자 주문 API")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "전체 주문 목록", description = "관리자용 전체 주문 조회 (A-020)")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(adminOrderService.getAllOrders(pageable)));
    }

    @PatchMapping("/{orderId}/state")
    @Operation(summary = "주문 상태 변경", description = "주문 상태 직접 변경 (A-021)")
    public ResponseEntity<ApiResponse<OrderResponse>> changeState(
            @PathVariable Integer orderId,
            @RequestParam String state) {
        return ResponseEntity.ok(ApiResponse.ok(adminOrderService.changeOrderState(orderId, state)));
    }

    @PostMapping("/{orderId}/tracking")
    @Operation(summary = "송장 등록", description = "배송사 + 운송장번호 등록 후 SHIPPED 처리 (A-022)")
    public ResponseEntity<ApiResponse<Void>> registerTracking(
            @PathVariable Integer orderId,
            @RequestParam String carrier,
            @RequestParam String trackingNumber) {
        adminOrderService.registerTracking(orderId, carrier, trackingNumber);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/returns")
    @Operation(summary = "반품/교환 요청 목록", description = "전체 반품·교환 요청 조회 (A-023)")
    public ResponseEntity<ApiResponse<Page<ReturnRequestResponse>>> getReturnRequests(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(adminOrderService.getReturnRequests(pageable)));
    }

    @PatchMapping("/returns/{returnId}")
    @Operation(summary = "반품/교환 승인 또는 거절", description = "반품·교환 요청 처리 (A-024)")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> processReturn(
            @PathVariable Integer returnId,
            @RequestParam boolean approve) {
        return ResponseEntity.ok(ApiResponse.ok(adminOrderService.processReturn(returnId, approve)));
    }
}
