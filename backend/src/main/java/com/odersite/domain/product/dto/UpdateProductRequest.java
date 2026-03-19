package com.odersite.domain.product.dto;

import lombok.Getter;

@Getter
public class UpdateProductRequest {
    private String productName;
    private String productDescription;
    private Integer productPrice;
    private Integer categoryId;
}
