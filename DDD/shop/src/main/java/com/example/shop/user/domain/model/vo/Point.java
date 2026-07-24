package com.example.shop.user.domain.model.vo;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;

/**
 * 포인트 값 객체. 불변이며 값으로 비교한다.
 * 잔액은 항상 0 이상이라는 불변식을 스스로 지킨다.
 */
public record Point(long value) {

    public Point {
        if (value < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
    }

    public static Point zero() {
        return new Point(0L);
    }

    public static Point of(long value) {
        return new Point(value);
    }

    /** user-04 : 충전 (충전 금액 > 0) */
    public Point charge(long amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_CHARGE_POINT);
        }
        return new Point(this.value + amount);
    }

    /** 주문 시 차감 (부족하면 실패) */
    public Point use(long amount) {
        if (this.value < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }
        return new Point(this.value - amount);
    }

    /** 주문 취소 시 환불 */
    public Point refund(long amount) {
        return new Point(this.value + amount);
    }
}
