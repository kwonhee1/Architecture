package com.example.shop.coupon.domain.model.vo;

/**
 * 금액 값 객체. 불변이며 값으로 비교한다. 음수 금액은 존재할 수 없다.
 * (쿠폰은 % 할인이 아니라 금액 할인이다.)
 */
public record Money(long value) {

    public Money {
        if (value < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }
    }

    public static Money of(long value) {
        return new Money(value);
    }

    public boolean isPositive() {
        return value > 0;
    }
}
