package com.example.shop.coupon.domain.model;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.model.vo.Money;
import com.example.shop.coupon.domain.model.vo.OwnerId;

/**
 * coupon aggregate root.
 *
 * <p>금액 할인 쿠폰(% 아님). "사용 가능 여부", "본인 쿠폰만 사용", "이미 쓴 쿠폰은 못 씀"
 * 같은 규칙을 스스로 소유한다. 소유자(user)는 객체가 아니라 식별자(OwnerId)로 참조한다.</p>
 */
public class Coupon {

    /** coupon-01 : 회원 가입 시 자동 발급되는 쿠폰 스펙 */
    private static final String SIGNUP_COUPON_NAME = "회원 가입 쿠폰";
    private static final long SIGNUP_COUPON_AMOUNT = 1000L;

    private final CouponId id;      // 신규 발급 시 null, 복원 시 존재
    private final OwnerId ownerId;
    private final String name;
    private final Money discount;
    private boolean used;

    private Coupon(CouponId id, OwnerId ownerId, String name, Money discount, boolean used) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("쿠폰 이름은 비어 있을 수 없습니다.");
        }
        if (!discount.isPositive()) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_AMOUNT);
        }
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.discount = discount;
        this.used = used;
    }

    /** 일반 발급 (할인 금액 > 0). */
    public static Coupon issue(OwnerId ownerId, String name, Money discount) {
        return new Coupon(null, ownerId, name, discount, false);
    }

    /** coupon-01 : 회원 가입 쿠폰 발급. {"회원 가입 쿠폰", 1000} */
    public static Coupon issueSignupCoupon(OwnerId ownerId) {
        return new Coupon(null, ownerId, SIGNUP_COUPON_NAME, Money.of(SIGNUP_COUPON_AMOUNT), false);
    }

    /** 영속 상태로부터 복원 (infrastructure 전용). */
    public static Coupon reconstitute(CouponId id, OwnerId ownerId, String name, Money discount, boolean used) {
        return new Coupon(id, ownerId, name, discount, used);
    }

    /** 주문 시 사용 (본인 쿠폰만 · 미사용 상태만) → 할인 금액 반환. */
    public Money use(OwnerId requester) {
        if (!this.ownerId.equals(requester)) {
            throw new BusinessException(ErrorCode.NOT_COUPON_OWNER);
        }
        if (this.used) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }
        this.used = true;
        return this.discount;
    }

    /** 주문 취소 시 사용 가능 상태로 되돌린다. */
    public void restore() {
        this.used = false;
    }

    public boolean isAvailable() {
        return !used;
    }

    public CouponId id() {
        return id;
    }

    public OwnerId ownerId() {
        return ownerId;
    }

    public String name() {
        return name;
    }

    public Money discount() {
        return discount;
    }

    public boolean isUsed() {
        return used;
    }
}
