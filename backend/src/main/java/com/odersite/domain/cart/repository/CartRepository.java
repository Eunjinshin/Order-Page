package com.odersite.domain.cart.repository;

import com.odersite.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c JOIN FETCH c.productOption o JOIN FETCH o.product WHERE c.memberUser.userId = :userId")
    List<Cart> findByUserId(Integer userId);

    Optional<Cart> findByMemberUser_UserIdAndProductOption_OptionId(Integer userId, Integer optionId);
}
