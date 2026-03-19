package com.odersite.domain.order.entity;

import com.odersite.domain.coupon.entity.Coupon;
import com.odersite.domain.member.entity.MemberAddress;
import com.odersite.domain.member.entity.MemberUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orders {

    public enum OrderState {
        PENDING, PAID, PREPARING, SHIPPED, DELIVERED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MemberUser memberUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private MemberAddress memberAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "discount_price")
    @Builder.Default
    private Integer discountPrice = 0;

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    @Builder.Default
    private OrderState orderState = OrderState.PENDING;

    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDateTime.now();
    }

    public void changeState(OrderState newState) {
        this.orderState = newState;
    }

    public boolean isCancellable() {
        return orderState == OrderState.PENDING || orderState == OrderState.PAID
                || orderState == OrderState.PREPARING;
    }
}
