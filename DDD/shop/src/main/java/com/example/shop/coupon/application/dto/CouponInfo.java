package com.example.shop.coupon.application.dto;

import com.example.shop.coupon.domain.model.Coupon;

/** coupon-02 결과 { 쿠폰 id, 이름, 할인 금액 }. presentation 으로 domain 대신 이 DTO 를 넘긴다. */
public record CouponInfo(Long id, String name, long discountAmount) {

    public static CouponInfo from(Coupon coupon) {
        return new CouponInfo(
                coupon.id().value(),
                coupon.name(),
                coupon.discount().value()
        );
    }
}
