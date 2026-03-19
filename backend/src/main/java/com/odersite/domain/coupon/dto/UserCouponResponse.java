package com.odersite.domain.coupon.dto;

import com.odersite.domain.coupon.entity.Coupon;
import com.odersite.domain.coupon.entity.UserCoupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {
    private final Integer userCouponId;
    private final Integer couponId;
    private final String name;
    private final String discountType;
    private final Integer discountValue;
    private final Integer minOrderAmount;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;
    private final Boolean isUsed;

    public UserCouponResponse(UserCoupon uc) {
        Coupon c = uc.getCoupon();
        this.userCouponId = uc.getUserCouponId();
        this.couponId = c.getCouponId();
        this.name = c.getName();
        this.discountType = c.getDiscountType().name();
        this.discountValue = c.getDiscountValue();
        this.minOrderAmount = c.getMinOrderAmount();
        this.validFrom = c.getValidFrom();
        this.validUntil = c.getValidUntil();
        this.isUsed = uc.getIsUsed();
    }
}
