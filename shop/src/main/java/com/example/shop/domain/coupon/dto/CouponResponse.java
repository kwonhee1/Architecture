package com.example.shop.domain.coupon.dto;

import com.example.shop.domain.coupon.entity.Coupon;

import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String name,
        int discountAmount,
        int minOrderAmount,
        LocalDateTime expiresAt,
        boolean active
) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getMinOrderAmount(),
                coupon.getExpiresAt(),
                coupon.isActive()
        );
    }
}
