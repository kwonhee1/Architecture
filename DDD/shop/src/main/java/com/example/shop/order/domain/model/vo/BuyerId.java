package com.example.shop.order.domain.model.vo;

/** 주문자(회원) 식별자. user aggregate 를 식별자로만 참조한다. */
public record BuyerId(Long value) {

    public BuyerId {
        if (value == null) {
            throw new IllegalArgumentException("BuyerId 는 null 일 수 없습니다.");
        }
    }

    public static BuyerId of(Long value) {
        return new BuyerId(value);
    }
}
