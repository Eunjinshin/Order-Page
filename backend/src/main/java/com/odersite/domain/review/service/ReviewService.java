package com.odersite.domain.review.service;

import com.odersite.domain.member.entity.MemberUser;
import com.odersite.domain.member.repository.MemberUserRepository;
import com.odersite.domain.order.entity.OrderItem;
import com.odersite.domain.order.repository.OrderItemRepository;
import com.odersite.domain.product.entity.Product;
import com.odersite.domain.product.repository.ProductRepository;
import com.odersite.domain.review.dto.CreateReviewRequest;
import com.odersite.domain.review.dto.ReviewResponse;
import com.odersite.domain.review.entity.Review;
import com.odersite.domain.review.entity.ReviewHelpful;
import com.odersite.domain.review.entity.ReviewImage;
import com.odersite.domain.review.repository.ReviewHelpfulRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final MemberUserRepository memberUserRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public Page<ReviewResponse> getProductReviews(Integer productId, Pageable pageable) {
        return reviewRepository.findByProduct_ProductIdAndIsBlindFalse(productId, pageable)
                .map(ReviewResponse::new);
    }

    @Transactional
    public ReviewResponse createReview(Integer userId, CreateReviewRequest request) {
        MemberUser user = memberUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 실구매 검증 (F-050)
        if (!orderItem.getOrders().getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (reviewRepository.existsByOrderItem_OrderItemId(request.getOrderItemId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        Review review = Review.builder()
                .memberUser(user)
                .product(product)
                .orderItem(orderItem)
                .rating(request.getRating())
                .content(request.getContent())
                .build();
        reviewRepository.save(review);

        // 이미지 저장 (최대 5장)
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ReviewImage image = ReviewImage.builder()
                        .review(review)
                        .imageUrl(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build();
                review.getImages().add(image);
            }
        }

        return new ReviewResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Integer userId, Integer reviewId, String content, Integer rating) {
        Review review = findReview(userId, reviewId);
        review.update(content, rating);
        return new ReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Integer userId, Integer reviewId) {
        Review review = findReview(userId, reviewId);
        reviewRepository.delete(review);
    }

    @Transactional
    public void toggleHelpful(Integer userId, Integer reviewId) {
        if (reviewHelpfulRepository.existsByReview_ReviewIdAndMemberUser_UserId(reviewId, userId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        MemberUser user = memberUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        reviewHelpfulRepository.save(ReviewHelpful.builder()
                .review(review).memberUser(user).build());
    }

    private Review findReview(Integer userId, Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!review.getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return review;
    }
}
