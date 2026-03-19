package com.odersite.domain.coupon.repository;

import com.odersite.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    List<UserCoupon> findByMemberUser_UserIdAndIsUsedFalse(Integer userId);
    Optional<UserCoupon> findByMemberUser_UserIdAndCoupon_CouponId(Integer userId, Integer couponId);
}
