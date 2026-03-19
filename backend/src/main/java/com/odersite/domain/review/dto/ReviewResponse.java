package com.odersite.domain.review.dto;

import com.odersite.domain.review.entity.Review;
import com.odersite.domain.review.entity.ReviewImage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewResponse {
    private final Integer reviewId;
    private final Integer userId;
    private final String nickname;
    private final Integer productId;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final int helpfulCount;
    private final List<String> imageUrls;
    private final String adminReply;

    public ReviewResponse(Review r) {
        this.reviewId = r.getReviewId();
        this.userId = r.getMemberUser().getUserId();
        this.nickname = r.getMemberUser().getNickname();
        this.productId = r.getProduct().getProductId();
        this.rating = r.getRating();
        this.content = r.getContent();
        this.createdAt = r.getCreatedAt();
        this.helpfulCount = r.getHelpfuls().size();
        this.imageUrls = r.getImages().stream().map(ReviewImage::getImageUrl).toList();
        this.adminReply = r.getReply() != null ? r.getReply().getContent() : null;
    }
}
