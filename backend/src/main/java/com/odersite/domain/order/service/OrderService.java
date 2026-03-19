package com.odersite.domain.order.service;

import com.odersite.domain.coupon.entity.Coupon;
import com.odersite.domain.coupon.entity.UserCoupon;
import com.odersite.domain.coupon.repository.CouponRepository;
import com.odersite.domain.coupon.repository.UserCouponRepository;
import com.odersite.domain.order.dto.CreateOrderRequest;
import com.odersite.domain.order.dto.OrderResponse;
import com.odersite.domain.order.dto.ReturnRequestDto;
import com.odersite.domain.order.entity.OrderItem;
import com.odersite.domain.order.entity.Orders;
import com.odersite.domain.order.entity.ReturnRequest;
import com.odersite.domain.order.repository.OrderItemRepository;
import com.odersite.domain.order.repository.OrdersRepository;
import com.odersite.domain.order.repository.ReturnRequestRepository;
import com.odersite.domain.member.entity.MemberAddress;
import com.odersite.domain.member.entity.MemberUser;
import com.odersite.domain.member.repository.MemberAddressRepository;
import com.odersite.domain.member.repository.MemberUserRepository;
import com.odersite.domain.product.entity.ProductOption;
import com.odersite.domain.product.repository.ProductOptionRepository;
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
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final MemberUserRepository memberUserRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        MemberUser user = memberUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberAddress address = memberAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 총 금액 계산
        int totalPrice = 0;
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            ProductOption option = productOptionRepository.findById(itemReq.getOptionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            if (option.getStockQty() < itemReq.getQuantity()) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }
            totalPrice += option.getProduct().getProductPrice() * itemReq.getQuantity();
        }

        // 쿠폰 할인 계산 (F-023)
        int discountPrice = 0;
        Coupon coupon = null;
        if (request.getCouponId() != null) {
            coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            UserCoupon userCoupon = userCouponRepository
                    .findByMemberUser_UserIdAndCoupon_CouponId(userId, coupon.getCouponId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            if (userCoupon.getIsUsed() || !coupon.isValid()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            if (totalPrice < coupon.getMinOrderAmount()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            discountPrice = coupon.calculateDiscount(totalPrice);
            userCoupon.use();
        }

        Orders order = Orders.builder()
                .memberUser(user)
                .memberAddress(address)
                .coupon(coupon)
                .totalPrice(totalPrice)
                .discountPrice(discountPrice)
                .finalPrice(totalPrice - discountPrice)
                .build();
        ordersRepository.save(order);

        // 주문 항목 생성 + 재고 차감
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            ProductOption option = productOptionRepository.findById(itemReq.getOptionId()).get();
            option.decreaseStock(itemReq.getQuantity());

            OrderItem item = OrderItem.builder()
                    .orders(order)
                    .productOption(option)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(option.getProduct().getProductPrice())
                    .build();
            orderItemRepository.save(item);
        }

        return new OrderResponse(order);
    }

    public Page<OrderResponse> getMyOrders(Integer userId, Pageable pageable) {
        return ordersRepository.findByUserId(userId, pageable).map(OrderResponse::new);
    }

    public OrderResponse getOrder(Integer userId, Integer orderId) {
        Orders order = findOrder(userId, orderId);
        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Integer userId, Integer orderId) {
        Orders order = findOrder(userId, orderId);
        if (!order.isCancellable()) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }
        // 재고 복구
        for (OrderItem item : order.getItems()) {
            item.getProductOption().increaseStock(item.getQuantity());
        }
        order.changeState(Orders.OrderState.CANCELLED);
        return new OrderResponse(order);
    }

    @Transactional
    public ReturnRequest requestReturn(Integer userId, Integer orderItemId, ReturnRequestDto dto) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!orderItem.getOrders().getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (orderItem.getOrders().getOrderState() != Orders.OrderState.DELIVERED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        return returnRequestRepository.save(ReturnRequest.builder()
                .orderItem(orderItem)
                .type(ReturnRequest.ReturnType.valueOf(dto.getType()))
                .reason(dto.getReason())
                .build());
    }

    private Orders findOrder(Integer userId, Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return order;
    }
}
