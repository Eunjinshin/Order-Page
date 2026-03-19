package com.odersite.domain.cart.service;

import com.odersite.domain.cart.dto.AddCartRequest;
import com.odersite.domain.cart.dto.CartItemResponse;
import com.odersite.domain.cart.entity.Cart;
import com.odersite.domain.cart.repository.CartRepository;
import com.odersite.domain.member.entity.MemberUser;
import com.odersite.domain.member.repository.MemberUserRepository;
import com.odersite.domain.product.entity.ProductOption;
import com.odersite.domain.product.repository.ProductOptionRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MemberUserRepository memberUserRepository;
    private final ProductOptionRepository productOptionRepository;

    public List<CartItemResponse> getCart(Integer userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(CartItemResponse::new).toList();
    }

    @Transactional
    public CartItemResponse addToCart(Integer userId, AddCartRequest request) {
        ProductOption option = productOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (option.getStockQty() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // 동일 옵션 → 수량 합산 (F-020)
        Optional<Cart> existing = cartRepository.findByMemberUser_UserIdAndProductOption_OptionId(
                userId, request.getOptionId());

        if (existing.isPresent()) {
            existing.get().addQuantity(request.getQuantity());
            return new CartItemResponse(existing.get());
        }

        MemberUser user = memberUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Cart cart = Cart.builder()
                .memberUser(user)
                .productOption(option)
                .quantity(request.getQuantity())
                .build();

        return new CartItemResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartItemResponse updateQuantity(Integer userId, Integer cartId, int quantity) {
        Cart cart = findCart(userId, cartId);

        if (cart.getProductOption().getStockQty() < quantity) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        cart.updateQuantity(quantity);
        return new CartItemResponse(cart);
    }

    @Transactional
    public void deleteCartItem(Integer userId, Integer cartId) {
        Cart cart = findCart(userId, cartId);
        cartRepository.delete(cart);
    }

    @Transactional
    public void deleteAll(Integer userId) {
        List<Cart> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
    }

    private Cart findCart(Integer userId, Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!cart.getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return cart;
    }
}
