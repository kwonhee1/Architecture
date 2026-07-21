package com.example.shop.domain.user.service.vo;

import com.example.shop.domain.user.entity.Coupon;

import java.util.Map;

/**
 * coupon 이 port 경계 밖으로 내보내는 VO 모음. Coupon entity 를 담지 않는다.
 * <p>
 * 여기 있는 타입은 port 의 반환값 전용이다. controller 로 나가는 응답 모양은 dto 가 따로 맡는다
 * ({@link CouponInfo} → CouponResponse).
 */
public final class CouponVo {

    private CouponVo() {}

    public record CouponInfo(Long id, String name, int discountAmount) {
        public static CouponInfo of(Coupon coupon) {
            return new CouponInfo(coupon.getId(), coupon.getName(), coupon.getDiscountAmount());
        }
    }

    /** 쿠폰은 선택이므로 "쓴 쿠폰 없음" 도 정상 결과다 (coupon 이 null, 할인 0) */
    public record CouponUseResult(CouponInfo coupon, int discountAmount) {
        public static CouponUseResult none() {
            return new CouponUseResult(null, 0);
        }
    }

    public record CouponRestoreResult(CouponInfo coupon, boolean usable) {
        public static CouponRestoreResult none() {
            return new CouponRestoreResult(null, false);
        }
    }
}
