package com.odersite.domain.review.controller;

import com.odersite.domain.review.dto.CreateReviewRequest;
import com.odersite.domain.review.dto.ReviewResponse;
import com.odersite.domain.review.service.ReviewService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/v1/products/{productId}/reviews")
    @Operation(summary = "상품 리뷰 목록 조회", description = "상품별 리뷰 페이지 조회 (F-060)")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable Integer productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getProductReviews(productId, pageable)));
    }

    @PostMapping("/api/v1/reviews")
    @Operation(summary = "리뷰 작성", description = "실구매 인증 후 리뷰 등록 (F-050, F-051)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            Authentication auth,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.createReview(userId(auth), request)));
    }

    @PatchMapping("/api/v1/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "본인 리뷰 내용/별점 수정 (F-052)")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            Authentication auth,
            @PathVariable Integer reviewId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer rating) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.updateReview(userId(auth), reviewId, content, rating)));
    }

    @DeleteMapping("/api/v1/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "본인 리뷰 삭제 (F-053)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            Authentication auth,
            @PathVariable Integer reviewId) {
        reviewService.deleteReview(userId(auth), reviewId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/v1/reviews/{reviewId}/helpful")
    @Operation(summary = "리뷰 도움됨", description = "리뷰 도움됨 표시 (중복 불가) (F-061)")
    public ResponseEntity<ApiResponse<Void>> toggleHelpful(
            Authentication auth,
            @PathVariable Integer reviewId) {
        reviewService.toggleHelpful(userId(auth), reviewId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
