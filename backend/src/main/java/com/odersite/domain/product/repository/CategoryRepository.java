package com.odersite.domain.product.repository;

import com.odersite.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentIsNullOrderBySortOrderAsc();
    List<Category> findByParent_CategoryIdOrderBySortOrderAsc(Integer parentId);
}
