package com.odersite.domain.delivery.repository;

import com.odersite.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    Optional<Delivery> findByOrders_OrderId(Integer orderId);
    Optional<Delivery> findByTrackingNumber(String trackingNumber);
}
