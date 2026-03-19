package com.odersite.domain.coupon.controller;

import com.odersite.domain.coupon.dto.UserCouponResponse;
import com.odersite.domain.coupon.service.CouponService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "쿠폰 API")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/me")
    @Operation(summary = "내 쿠폰 목록", description = "사용 가능한 보유 쿠폰 조회 (F-040)")
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> getMyCoupons(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(couponService.getMyAvailableCoupons(userId(auth))));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
