package com.odersite.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {

    @NotNull
    private Integer orderId;

    @NotNull
    private String paymentMethod;   // CARD | KAKAO | NAVER | TRANSFER | DEPOSIT

    private String pgProvider;      // TOSSPAYMENTS | KGINIIS
}
