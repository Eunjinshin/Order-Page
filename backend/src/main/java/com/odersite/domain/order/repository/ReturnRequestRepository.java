package com.odersite.domain.order.repository;

import com.odersite.domain.order.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {
    List<ReturnRequest> findByOrderItem_OrderItemId(Integer orderItemId);
}
