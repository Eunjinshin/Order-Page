package com.odersite.domain.order.service;

import com.odersite.domain.delivery.entity.Delivery;
import com.odersite.domain.delivery.repository.DeliveryRepository;
import com.odersite.domain.order.dto.OrderResponse;
import com.odersite.domain.order.dto.ReturnRequestResponse;
import com.odersite.domain.order.entity.Orders;
import com.odersite.domain.order.entity.ReturnRequest;
import com.odersite.domain.order.repository.OrdersRepository;
import com.odersite.domain.order.repository.ReturnRequestRepository;
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
public class AdminOrderService {

    private final OrdersRepository ordersRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final DeliveryRepository deliveryRepository;

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return ordersRepository.findAll(pageable).map(OrderResponse::new);
    }

    @Transactional
    public OrderResponse changeOrderState(Integer orderId, String state) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        Orders.OrderState newState = Orders.OrderState.valueOf(state.toUpperCase());
        order.changeState(newState);
        return new OrderResponse(order);
    }

    @Transactional
    public void registerTracking(Integer orderId, String carrier, String trackingNumber) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Delivery delivery = deliveryRepository.findByOrders_OrderId(orderId)
                .orElseGet(() -> deliveryRepository.save(
                        Delivery.builder().orders(order).carrier(carrier).build()));

        delivery.ship(trackingNumber);
        order.changeState(Orders.OrderState.SHIPPED);
    }

    @Transactional
    public ReturnRequestResponse processReturn(Integer returnId, boolean approve) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (approve) {
            returnRequest.approve();
        } else {
            returnRequest.reject();
        }
        return new ReturnRequestResponse(returnRequest);
    }

    public Page<ReturnRequestResponse> getReturnRequests(Pageable pageable) {
        return returnRequestRepository.findAll(pageable).map(ReturnRequestResponse::new);
    }
}
