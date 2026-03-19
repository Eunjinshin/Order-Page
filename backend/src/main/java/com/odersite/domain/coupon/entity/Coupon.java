package com.odersite.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "COUPON")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon {

    public enum DiscountType { RATE, FIXED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "discount_value")
    private Integer discountValue;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "min_order_amount")
    @Builder.Default
    private Integer minOrderAmount = 0;

    public int calculateDiscount(int totalPrice) {
        if (discountType == DiscountType.RATE) {
            return (int) (totalPrice * (discountValue / 100.0));
        }
        return discountValue;
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return validFrom.isBefore(now) && validUntil.isAfter(now);
    }
}
