package com.example.shop.order.domain.model.vo;

/** 주문 금액 값 객체. 음수가 될 수 없다 (쿠폰 적용 후에도 0 밑으로 내려가지 않는다). */
public record Money(long value) {

    public Money {
        if (value < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }
    }

    public static Money of(long value) {
        return new Money(value);
    }

    public static Money zero() {
        return new Money(0L);
    }

    public Money plus(Money other) {
        return new Money(this.value + other.value);
    }

    public Money times(long quantity) {
        return new Money(this.value * quantity);
    }

    /** 할인 적용. 음수로 내려가면 0 으로 고정한다. */
    public Money minusToZero(long discount) {
        long result = this.value - discount;
        return new Money(Math.max(result, 0));
    }
}
