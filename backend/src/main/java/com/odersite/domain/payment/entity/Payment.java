package com.odersite.domain.payment.entity;

import com.odersite.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    public enum PgProvider { TOSSPAYMENTS, KGINIIS }
    public enum PaymentMethod { CARD, KAKAO, NAVER, TRANSFER, DEPOSIT }
    public enum PaymentStatus { PENDING, SUCCESS, FAILED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider")
    private PgProvider pgProvider;

    @Column(name = "pg_transaction_id", length = 200)
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public void complete(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }
}
