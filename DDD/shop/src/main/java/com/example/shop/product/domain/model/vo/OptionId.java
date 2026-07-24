package com.example.shop.product.domain.model.vo;

public record OptionId(Long value) {

    public OptionId {
        if (value == null) {
            throw new IllegalArgumentException("OptionId 는 null 일 수 없습니다.");
        }
    }

    public static OptionId of(Long value) {
        return new OptionId(value);
    }
}
