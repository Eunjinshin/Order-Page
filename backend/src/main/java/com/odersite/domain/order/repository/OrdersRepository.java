package com.odersite.domain.order.repository;

import com.odersite.domain.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    @Query("SELECT o FROM Orders o WHERE o.memberUser.userId = :userId ORDER BY o.orderedAt DESC")
    Page<Orders> findByUserId(Integer userId, Pageable pageable);
}
