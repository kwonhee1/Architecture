package com.example.shop.domain.user.entity;

/** coupon-02 응답 { 쿠폰 id, 이름, 할인 금액 } */
// 원래 info -> dto 분리해야하는데 사용하는 사용하는 dto가 한개라 분리 안함 (vo, dto 통합 사용)
public record CouponInfo(
        Long id,
        String name,
        int discountAmount
) {
    public static CouponInfo from(Coupon coupon) {
        return new CouponInfo(coupon.getId(), coupon.getName(), coupon.getDiscountAmount());
    }
}
