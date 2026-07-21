package com.example.shop.domain.user.dto;

import com.example.shop.domain.user.entity.Coupon;
import com.example.shop.domain.user.service.vo.CouponVo.CouponInfo;

/** coupon-02 응답 { 쿠폰 id, 이름, 할인 금액 } (order-02 응답의 쿠폰 자리에서도 쓴다) */
public record CouponResponse(
        Long id,
        String name,
        int discountAmount
) {
    /** 같은 domain 안에서는 entity 를 그대로 옮긴다 */
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getName(), coupon.getDiscountAmount());
    }

    /** port 가 내준 경계 VO 를 응답 모양으로 옮긴다. 쿠폰은 선택이라 없으면 null 이다. (order-02 용) */
    public static CouponResponse from(CouponInfo info) {
        if (info == null) {
            return null;
        }
        return new CouponResponse(info.id(), info.name(), info.discountAmount());
    }
}
