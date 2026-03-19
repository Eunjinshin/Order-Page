package com.odersite.domain.payment.dto;

import com.odersite.domain.payment.entity.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private final Integer paymentId;
    private final Integer orderId;
    private final String pgProvider;
    private final String pgTransactionId;
    private final String paymentMethod;
    private final Integer amount;
    private final String status;
    private final LocalDateTime paidAt;

    public PaymentResponse(Payment p) {
        this.paymentId = p.getPaymentId();
        this.orderId = p.getOrders().getOrderId();
        this.pgProvider = p.getPgProvider() != null ? p.getPgProvider().name() : null;
        this.pgTransactionId = p.getPgTransactionId();
        this.paymentMethod = p.getPaymentMethod().name();
        this.amount = p.getAmount();
        this.status = p.getStatus().name();
        this.paidAt = p.getPaidAt();
    }
}
