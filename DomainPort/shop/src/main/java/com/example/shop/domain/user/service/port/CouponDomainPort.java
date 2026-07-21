package com.example.shop.domain.user.service.port;

import com.example.shop.domain.user.service.vo.CouponVo.*;

import java.util.List;

/**
 * 타 domain ↔ coupon service 계약.
 * <p>
 * order 는 쿠폰이 유효한지 스스로 판단하지 않는다. useCoupon 을 호출하고
 * 결과 VO 가 설명해 주는 할인 금액만 받아 간다.
 */
public interface CouponDomainPort {

    /** 쿠폰 정보 일괄 조회 (order-02 응답의 쿠폰 정보 용) */
    List<CouponInfo> getCouponInfos(List<Long> couponIds);

    /** 쿠폰 사용. 소유자/사용여부 판단은 coupon 이 한다. couponId 가 null 이면 할인 0 인 결과. */
    CouponUseResult useCoupon(Long couponId, Long userId);

    /** 주문 취소 시 쿠폰을 사용 가능 상태로 복원. couponId 가 null 이면 되돌릴 쿠폰이 없는 결과. */
    CouponRestoreResult restoreCoupon(Long couponId);
}
