package com.odersite.domain.order.controller;

import com.odersite.domain.order.dto.CreateOrderRequest;
import com.odersite.domain.order.dto.OrderResponse;
import com.odersite.domain.order.dto.ReturnRequestDto;
import com.odersite.domain.order.service.OrderService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "배송지/옵션/쿠폰 포함 주문 생성 (F-022, F-023)")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Authentication auth,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.createOrder(userId(auth), request)));
    }

    @GetMapping
    @Operation(summary = "주문 내역 조회", description = "내 주문 목록, 최신순 (F-024)")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                orderService.getMyOrders(userId(auth), PageRequest.of(page, size))));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보 (F-024)")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            Authentication auth,
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrder(userId(auth), orderId)));
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "배송 준비 전까지 취소 가능, 재고 자동 복구 (F-025)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            Authentication auth,
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancelOrder(userId(auth), orderId)));
    }

    @PostMapping("/items/{orderItemId}/returns")
    @Operation(summary = "반품/교환 신청", description = "배송 완료 후 7일 이내 신청 (F-026)")
    public ResponseEntity<ApiResponse<Void>> requestReturn(
            Authentication auth,
            @PathVariable Integer orderItemId,
            @Valid @RequestBody ReturnRequestDto dto) {
        orderService.requestReturn(userId(auth), orderItemId, dto);
        return ResponseEntity.ok(ApiResponse.ok("반품/교환 신청이 완료되었습니다.", null));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
