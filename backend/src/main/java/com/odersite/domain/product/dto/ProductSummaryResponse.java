package com.odersite.domain.product.dto;

import com.odersite.domain.product.entity.Product;
import com.odersite.domain.product.entity.ProductImage;
import lombok.Getter;

@Getter
public class ProductSummaryResponse {
    private final Integer productId;
    private final String productName;
    private final Integer productPrice;
    private final String mainImageUrl;
    private final String categoryName;

    public ProductSummaryResponse(Product p) {
        this.productId = p.getProductId();
        this.productName = p.getProductName();
        this.productPrice = p.getProductPrice();
        this.mainImageUrl = p.getImages().stream()
                .filter(ProductImage::getIsMain)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
        this.categoryName = p.getCategory().getCategoryName();
    }
}
