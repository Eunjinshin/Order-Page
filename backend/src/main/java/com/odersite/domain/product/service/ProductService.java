package com.odersite.domain.product.service;

import com.odersite.domain.product.dto.ProductDetailResponse;
import com.odersite.domain.product.dto.ProductSummaryResponse;
import com.odersite.domain.product.entity.Product;
import com.odersite.domain.product.repository.CategoryRepository;
import com.odersite.domain.product.repository.ProductRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductSummaryResponse> getProducts(Integer categoryId, String keyword, Pageable pageable) {
        return productRepository.findByFilter(categoryId, keyword, pageable)
                .map(ProductSummaryResponse::new);
    }

    public ProductDetailResponse getProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.getIsVisible()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return new ProductDetailResponse(product);
    }
}
