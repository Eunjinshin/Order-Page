package com.odersite.domain.coupon.service;

import com.odersite.domain.coupon.dto.UserCouponResponse;
import com.odersite.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final UserCouponRepository userCouponRepository;

    public List<UserCouponResponse> getMyAvailableCoupons(Integer userId) {
        return userCouponRepository.findByMemberUser_UserIdAndIsUsedFalse(userId)
                .stream()
                .filter(uc -> uc.getCoupon().isValid())
                .map(UserCouponResponse::new)
                .toList();
    }
}
