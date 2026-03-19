package com.odersite.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_OPTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "stock_qty", nullable = false)
    @Builder.Default
    private Integer stockQty = 0;

    @Column(name = "alert_threshold")
    @Builder.Default
    private Integer alertThreshold = 10;

    public void decreaseStock(int qty) {
        if (this.stockQty < qty) {
            throw new IllegalStateException("재고 부족");
        }
        this.stockQty -= qty;
    }

    public void increaseStock(int qty) {
        this.stockQty += qty;
    }

    public void updateStock(int qty) {
        this.stockQty = qty;
    }

    public boolean isSoldOut() {
        return this.stockQty == 0;
    }
}
