package com.odersite.domain.review.repository;

import com.odersite.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findByProduct_ProductIdAndIsBlindFalse(Integer productId, Pageable pageable);
    boolean existsByOrderItem_OrderItemId(Integer orderItemId);
}
