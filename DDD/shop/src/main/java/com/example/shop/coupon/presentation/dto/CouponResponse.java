package com.example.shop.coupon.presentation.dto;

import com.example.shop.coupon.application.dto.CouponInfo;

/** coupon-02 응답 { 쿠폰 id, 이름, 할인 금액 } */
public record CouponResponse(Long id, String name, long discountAmount) {

    public static CouponResponse from(CouponInfo info) {
        return new CouponResponse(info.id(), info.name(), info.discountAmount());
    }
}
