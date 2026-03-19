package com.odersite.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateProductRequest {

    @NotNull
    private Integer shopId;

    @NotNull
    private Integer categoryId;

    @NotBlank
    private String productName;

    private String productDescription;

    @NotNull
    @Min(0)
    private Integer productPrice;

    private List<OptionRequest> options;
    private List<String> imageUrls;

    @Getter
    public static class OptionRequest {
        private String color;
        private String size;
        @Min(0)
        private Integer stockQty;
        private Integer alertThreshold;
    }
}
