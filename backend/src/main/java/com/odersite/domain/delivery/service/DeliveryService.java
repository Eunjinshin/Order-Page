package com.odersite.domain.delivery.service;

import com.odersite.domain.delivery.entity.Delivery;
import com.odersite.domain.delivery.repository.DeliveryRepository;
import com.odersite.domain.order.entity.Orders;
import com.odersite.domain.order.repository.OrdersRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrdersRepository ordersRepository;

    public Delivery getDelivery(Integer userId, Integer orderId) {
        Delivery delivery = deliveryRepository.findByOrders_OrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!delivery.getOrders().getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return delivery;
    }

    @Transactional
    public Delivery createDelivery(Integer orderId, String carrier) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Delivery delivery = Delivery.builder()
                .orders(order)
                .carrier(carrier)
                .build();
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateTrackingNumber(Integer deliveryId, String trackingNumber) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        delivery.ship(trackingNumber);
        delivery.getOrders().changeState(Orders.OrderState.SHIPPED);
        return delivery;
    }
}
