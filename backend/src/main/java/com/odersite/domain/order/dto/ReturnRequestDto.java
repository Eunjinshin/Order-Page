package com.odersite.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReturnRequestDto {

    @NotBlank
    private String type;  // RETURN | EXCHANGE

    private String reason;
}
