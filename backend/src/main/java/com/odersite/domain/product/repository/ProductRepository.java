package com.odersite.domain.product.repository;

import com.odersite.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.isVisible = true " +
           "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
           "AND (:keyword IS NULL OR p.productName LIKE %:keyword%)")
    Page<Product> findByFilter(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
