package com.example.shop.product.domain.model.vo;

public record ProductId(Long value) {

    public ProductId {
        if (value == null) {
            throw new IllegalArgumentException("ProductId 는 null 일 수 없습니다.");
        }
    }

    public static ProductId of(Long value) {
        return new ProductId(value);
    }
}
