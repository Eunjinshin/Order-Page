package com.odersite.domain.product.repository;

import com.odersite.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {

    @Query("SELECT o FROM ProductOption o WHERE o.product.productId = :productId AND o.stockQty <= o.alertThreshold")
    List<ProductOption> findLowStockByProductId(Integer productId);
}
