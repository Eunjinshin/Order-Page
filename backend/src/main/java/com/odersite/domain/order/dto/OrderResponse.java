package com.odersite.domain.order.dto;

import com.odersite.domain.order.entity.OrderItem;
import com.odersite.domain.order.entity.Orders;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {
    private final Integer orderId;
    private final String orderState;
    private final Integer totalPrice;
    private final Integer discountPrice;
    private final Integer finalPrice;
    private final LocalDateTime orderedAt;
    private final List<ItemDto> items;

    public OrderResponse(Orders o) {
        this.orderId = o.getOrderId();
        this.orderState = o.getOrderState().name();
        this.totalPrice = o.getTotalPrice();
        this.discountPrice = o.getDiscountPrice();
        this.finalPrice = o.getFinalPrice();
        this.orderedAt = o.getOrderedAt();
        this.items = o.getItems().stream().map(ItemDto::new).toList();
    }

    @Getter
    public static class ItemDto {
        private final Integer orderItemId;
        private final Integer optionId;
        private final String productName;
        private final Integer quantity;
        private final Integer unitPrice;

        public ItemDto(OrderItem i) {
            this.orderItemId = i.getOrderItemId();
            this.optionId = i.getProductOption().getOptionId();
            this.productName = i.getProductOption().getProduct().getProductName();
            this.quantity = i.getQuantity();
            this.unitPrice = i.getUnitPrice();
        }
    }
}
