package com.example.shop.domain.coupon.dto;

import com.example.shop.domain.coupon.entity.UserCoupon;

import java.time.LocalDateTime;

public record UserCouponResponse(
        Long id,
        CouponResponse coupon,
        boolean used,
        LocalDateTime issuedAt,
        LocalDateTime usedAt
) {
    public static UserCouponResponse from(UserCoupon userCoupon) {
        return new UserCouponResponse(
                userCoupon.getId(),
                CouponResponse.from(userCoupon.getCoupon()),
                userCoupon.isUsed(),
                userCoupon.getIssuedAt(),
                userCoupon.getUsedAt()
        );
    }
}
