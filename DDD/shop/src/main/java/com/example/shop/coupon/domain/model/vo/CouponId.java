package com.example.shop.coupon.domain.model.vo;

public record CouponId(Long value) {

    public CouponId {
        if (value == null) {
            throw new IllegalArgumentException("CouponId 는 null 일 수 없습니다.");
        }
    }

    public static CouponId of(Long value) {
        return new CouponId(value);
    }
}
