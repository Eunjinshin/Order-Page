package com.odersite.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequest {

    @NotBlank
    private String categoryName;

    private Integer parentId;
    private Integer depth;
    private Integer sortOrder;
}
