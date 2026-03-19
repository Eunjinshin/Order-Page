package com.odersite.domain.payment.controller;

import com.odersite.domain.payment.dto.PaymentRequest;
import com.odersite.domain.payment.dto.PaymentResponse;
import com.odersite.domain.payment.service.PaymentService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    @Operation(summary = "결제 준비", description = "PG사 결제 요청 전 PENDING 레코드 생성 (F-030, F-031)")
    public ResponseEntity<ApiResponse<PaymentResponse>> prepare(
            Authentication auth,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.preparePayment(userId(auth), request)));
    }

    @PostMapping("/{paymentId}/confirm")
    @Operation(summary = "결제 승인 처리", description = "PG사 콜백 후 주문 상태 PAID로 변경 (F-032)")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirm(
            @PathVariable Integer paymentId,
            @RequestParam(required = false) String pgTransactionId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.confirmPayment(paymentId, pgTransactionId)));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "결제 정보 조회 (영수증)", description = "주문별 결제 내역 조회 (F-034)")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            Authentication auth,
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPayment(userId(auth), orderId)));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
