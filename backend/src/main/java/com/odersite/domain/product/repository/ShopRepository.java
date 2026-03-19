package com.odersite.domain.product.repository;

import com.odersite.domain.product.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
}
