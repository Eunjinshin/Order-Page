package com.odersite.domain.product.dto;

import com.odersite.domain.product.entity.Product;
import com.odersite.domain.product.entity.ProductImage;
import com.odersite.domain.product.entity.ProductOption;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductDetailResponse {
    private final Integer productId;
    private final String productName;
    private final String productDescription;
    private final Integer productPrice;
    private final Integer categoryId;
    private final String categoryName;
    private final List<OptionDto> options;
    private final List<ImageDto> images;

    public ProductDetailResponse(Product p) {
        this.productId = p.getProductId();
        this.productName = p.getProductName();
        this.productDescription = p.getProductDescription();
        this.productPrice = p.getProductPrice();
        this.categoryId = p.getCategory().getCategoryId();
        this.categoryName = p.getCategory().getCategoryName();
        this.options = p.getOptions().stream().map(OptionDto::new).toList();
        this.images = p.getImages().stream()
                .sorted((a, b) -> a.getSortOrder() - b.getSortOrder())
                .map(ImageDto::new).toList();
    }

    @Getter
    public static class OptionDto {
        private final Integer optionId;
        private final String color;
        private final String size;
        private final Integer stockQty;
        private final boolean soldOut;

        public OptionDto(ProductOption o) {
            this.optionId = o.getOptionId();
            this.color = o.getColor();
            this.size = o.getSize();
            this.stockQty = o.getStockQty();
            this.soldOut = o.isSoldOut();
        }
    }

    @Getter
    public static class ImageDto {
        private final Integer imageId;
        private final String imageUrl;
        private final boolean isMain;

        public ImageDto(ProductImage i) {
            this.imageId = i.getImageId();
            this.imageUrl = i.getImageUrl();
            this.isMain = i.getIsMain();
        }
    }
}
