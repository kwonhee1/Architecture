package com.example.shop.order.domain.model.vo;

public record OrderId(Long value) {

    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("OrderId 는 null 일 수 없습니다.");
        }
    }

    public static OrderId of(Long value) {
        return new OrderId(value);
    }
}
