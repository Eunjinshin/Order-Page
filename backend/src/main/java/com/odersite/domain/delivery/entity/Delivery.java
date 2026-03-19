package com.odersite.domain.delivery.entity;

import com.odersite.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DELIVERY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Delivery {

    public enum DeliveryStatus { PREPARING, SHIPPED, IN_TRANSIT, DELIVERED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Integer deliveryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Column(name = "carrier", nullable = false, length = 50)
    private String carrier;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.PREPARING;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public void ship(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.deliveryStatus = DeliveryStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }

    public void deliver() {
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void updateStatus(DeliveryStatus status) {
        this.deliveryStatus = status;
    }
}
