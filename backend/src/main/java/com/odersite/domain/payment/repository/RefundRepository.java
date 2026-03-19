package com.odersite.domain.payment.repository;

import com.odersite.domain.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Integer> {
}
