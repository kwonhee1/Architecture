package com.example.shop.product.domain.model.vo;

/** 판매 금액 값 객체. 음수 금액은 존재할 수 없다. */
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

    /** 상품 금액 + 옵션 추가 금액. 추가 금액은 음수일 수 있으므로 결과가 음수가 될 수 있다. */
    public long plus(long additionalPrice) {
        return this.value + additionalPrice;
    }
}
