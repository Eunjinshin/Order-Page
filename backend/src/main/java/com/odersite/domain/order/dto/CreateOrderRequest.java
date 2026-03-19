package com.odersite.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateOrderRequest {

    @NotNull(message = "배송지를 선택해주세요.")
    private Integer addressId;

    private Integer couponId;

    @NotNull(message = "주문할 상품을 선택해주세요.")
    private List<OrderItemRequest> items;

    @Getter
    public static class OrderItemRequest {
        private Integer optionId;
        private Integer quantity;
    }
}
