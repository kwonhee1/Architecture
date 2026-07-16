package com.example.shop.domain.coupon.dto;

import com.example.shop.domain.coupon.entity.Coupon;

/** coupon-02 응답 { 쿠폰 id, 이름, 할인 금액 } */
public record CouponResponse(
        Long id,
        String name,
        int discountAmount
) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount()
        );
    }
}
