package com.odersite.domain.payment.repository;

import com.odersite.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrders_OrderId(Integer orderId);
}
