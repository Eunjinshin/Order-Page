package com.odersite.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StockUpdateRequest {

    @NotNull
    private Integer optionId;

    @NotNull
    @Min(0)
    private Integer stockQty;
}
