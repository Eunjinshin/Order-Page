package com.odersite.domain.delivery.controller;

import com.odersite.domain.delivery.entity.Delivery;
import com.odersite.domain.delivery.service.DeliveryService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "배송 API")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{orderId}/delivery")
    @Operation(summary = "배송 정보 조회", description = "택배사·운송장·상태 조회 (F-040, F-041)")
    public ResponseEntity<ApiResponse<Delivery>> getDelivery(
            Authentication auth,
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getDelivery(userId(auth), orderId)));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
