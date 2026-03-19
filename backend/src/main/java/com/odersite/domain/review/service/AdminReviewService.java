package com.odersite.domain.review.service;

import com.odersite.domain.review.dto.ReviewReplyRequest;
import com.odersite.domain.review.dto.ReviewResponse;
import com.odersite.domain.review.entity.Review;
import com.odersite.domain.review.entity.ReviewReply;
import com.odersite.domain.review.repository.ReviewReplyRepository;
import com.odersite.domain.review.repository.ReviewRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;

    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewResponse::new);
    }

    @Transactional
    public ReviewResponse blindReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        review.blind();
        return new ReviewResponse(review);
    }

    @Transactional
    public ReviewResponse unblindReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        review.unblind();
        return new ReviewResponse(review);
    }

    @Transactional
    public ReviewResponse addReply(Integer reviewId, Integer adminUserId, ReviewReplyRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (review.getReply() != null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        reviewReplyRepository.save(ReviewReply.builder()
                .review(review)
                .adminUserId(adminUserId)
                .content(request.getContent())
                .build());
        return new ReviewResponse(reviewRepository.findById(reviewId).orElseThrow());
    }

    @Transactional
    public void deleteReply(Integer replyId) {
        ReviewReply reply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        reviewReplyRepository.delete(reply);
    }
}
