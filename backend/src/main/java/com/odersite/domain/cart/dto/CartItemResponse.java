package com.odersite.domain.cart.dto;

import com.odersite.domain.cart.entity.Cart;
import com.odersite.domain.product.entity.ProductImage;
import lombok.Getter;

@Getter
public class CartItemResponse {
    private final Integer cartId;
    private final Integer optionId;
    private final Integer productId;
    private final String productName;
    private final Integer productPrice;
    private final String color;
    private final String size;
    private final Integer quantity;
    private final Integer stockQty;
    private final String mainImageUrl;

    public CartItemResponse(Cart c) {
        this.cartId = c.getCartId();
        this.optionId = c.getProductOption().getOptionId();
        this.productId = c.getProductOption().getProduct().getProductId();
        this.productName = c.getProductOption().getProduct().getProductName();
        this.productPrice = c.getProductOption().getProduct().getProductPrice();
        this.color = c.getProductOption().getColor();
        this.size = c.getProductOption().getSize();
        this.quantity = c.getQuantity();
        this.stockQty = c.getProductOption().getStockQty();
        this.mainImageUrl = c.getProductOption().getProduct().getImages().stream()
                .filter(ProductImage::getIsMain).findFirst()
                .map(ProductImage::getImageUrl).orElse(null);
    }
}
