package com.odersite.domain.review.repository;

import com.odersite.domain.review.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Integer> {
    boolean existsByReview_ReviewIdAndMemberUser_UserId(Integer reviewId, Integer userId);
}
