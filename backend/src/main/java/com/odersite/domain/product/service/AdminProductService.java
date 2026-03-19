package com.odersite.domain.product.service;

import com.odersite.domain.product.dto.CreateProductRequest;
import com.odersite.domain.product.dto.ProductDetailResponse;
import com.odersite.domain.product.dto.UpdateProductRequest;
import com.odersite.domain.product.entity.*;
import com.odersite.domain.product.repository.*;
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
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;

    public Page<ProductDetailResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDetailResponse::new);
    }

    @Transactional
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Product product = Product.builder()
                .shop(shop)
                .category(category)
                .productName(request.getProductName())
                .productDescription(request.getProductDescription())
                .productPrice(request.getProductPrice())
                .build();
        productRepository.save(product);

        if (request.getOptions() != null) {
            for (CreateProductRequest.OptionRequest opt : request.getOptions()) {
                ProductOption option = ProductOption.builder()
                        .product(product)
                        .color(opt.getColor())
                        .size(opt.getSize())
                        .stockQty(opt.getStockQty() != null ? opt.getStockQty() : 0)
                        .alertThreshold(opt.getAlertThreshold() != null ? opt.getAlertThreshold() : 10)
                        .build();
                product.getOptions().add(option);
            }
        }

        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build();
                product.getImages().add(image);
            }
        }

        return new ProductDetailResponse(product);
    }

    @Transactional
    public ProductDetailResponse updateProduct(Integer productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        Category category = request.getCategoryId() != null
                ? categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND))
                : product.getCategory();
        product.update(
                request.getProductName() != null ? request.getProductName() : product.getProductName(),
                request.getProductDescription() != null ? request.getProductDescription() : product.getProductDescription(),
                request.getProductPrice() != null ? request.getProductPrice() : product.getProductPrice(),
                category
        );
        return new ProductDetailResponse(product);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    @Transactional
    public void toggleVisibility(Integer productId, boolean visible) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.toggleVisibility(visible);
    }

    @Transactional
    public void updateStock(Integer optionId, int stockQty) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        option.updateStock(stockQty);
    }
}
