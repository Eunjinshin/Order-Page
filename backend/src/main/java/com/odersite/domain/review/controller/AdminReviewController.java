package com.odersite.domain.review.controller;

import com.odersite.domain.review.dto.ReviewReplyRequest;
import com.odersite.domain.review.dto.ReviewResponse;
import com.odersite.domain.review.service.AdminReviewService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@Tag(name = "Admin-Review", description = "관리자 리뷰 API")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @GetMapping
    @Operation(summary = "전체 리뷰 목록", description = "관리자용 전체 리뷰 조회 (A-040)")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getAllReviews(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(adminReviewService.getAllReviews(pageable)));
    }

    @PatchMapping("/{reviewId}/blind")
    @Operation(summary = "리뷰 블라인드", description = "리뷰 블라인드 처리 (A-041)")
    public ResponseEntity<ApiResponse<ReviewResponse>> blindReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(ApiResponse.ok(adminReviewService.blindReview(reviewId)));
    }

    @PatchMapping("/{reviewId}/unblind")
    @Operation(summary = "리뷰 블라인드 해제", description = "리뷰 블라인드 해제 (A-042)")
    public ResponseEntity<ApiResponse<ReviewResponse>> unblindReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(ApiResponse.ok(adminReviewService.unblindReview(reviewId)));
    }

    @PostMapping("/{reviewId}/reply")
    @Operation(summary = "리뷰 답글 등록", description = "관리자 리뷰 답글 작성 (A-043)")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReply(
            Authentication auth,
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewReplyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminReviewService.addReply(reviewId, userId(auth), request)));
    }

    @DeleteMapping("/replies/{replyId}")
    @Operation(summary = "리뷰 답글 삭제", description = "관리자 리뷰 답글 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteReply(@PathVariable Integer replyId) {
        adminReviewService.deleteReply(replyId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
