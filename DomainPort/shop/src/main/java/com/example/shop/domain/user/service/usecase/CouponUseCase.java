package com.example.shop.domain.user.service.usecase;

import com.example.shop.domain.user.dto.CouponResponse;

import java.util.List;

/**
 * controller ↔ coupon service 계약.
 * <p>
 * controller 가 직접 호출하는 자리라 반환값은 응답 dto 다 (경계 VO 는 port 쪽 언어다).
 */
public interface CouponUseCase {

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회 */
    List<CouponResponse> getMyCoupons(Long userId);
}
