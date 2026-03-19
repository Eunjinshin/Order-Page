package com.odersite.domain.cart.controller;

import com.odersite.domain.cart.dto.AddCartRequest;
import com.odersite.domain.cart.dto.CartItemResponse;
import com.odersite.domain.cart.service.CartService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 API")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "내 장바구니 목록 (F-020)")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCart(userId(auth))));
    }

    @PostMapping
    @Operation(summary = "장바구니 담기", description = "동일 옵션 재담기 시 수량 합산 (F-020)")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            Authentication auth,
            @Valid @RequestBody AddCartRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.addToCart(userId(auth), request)));
    }

    @PatchMapping("/{cartId}")
    @Operation(summary = "장바구니 수량 변경", description = "재고 초과 시 경고 (F-021)")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            Authentication auth,
            @PathVariable Integer cartId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.updateQuantity(userId(auth), cartId, quantity)));
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "장바구니 항목 삭제", description = "단건 삭제 (F-021)")
    public ResponseEntity<ApiResponse<Void>> deleteItem(Authentication auth, @PathVariable Integer cartId) {
        cartService.deleteCartItem(userId(auth), cartId);
        return ResponseEntity.ok(ApiResponse.ok("삭제되었습니다.", null));
    }

    @DeleteMapping
    @Operation(summary = "장바구니 전체 삭제", description = "전체 항목 삭제 (F-021)")
    public ResponseEntity<ApiResponse<Void>> deleteAll(Authentication auth) {
        cartService.deleteAll(userId(auth));
        return ResponseEntity.ok(ApiResponse.ok("장바구니가 비워졌습니다.", null));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
