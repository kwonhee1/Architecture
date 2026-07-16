package com.example.shop.domain.product.dto;

import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.user.entity.User;

/** 상품 등록/수정/목록 응답 (옵션 미포함) */
public record ProductResponse(
        Long id,
        String description,
        int price,
        Creator creator
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                Creator.from(product.getCreator())
        );
    }
}
