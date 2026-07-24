package com.example.shop.product.application.dto;

import com.example.shop.product.domain.model.Product;

/** 상품 정보 { id, 설명, 금액, 생성자 이름 } (옵션 미포함). */
public record ProductInfo(Long id, String description, long price, String creatorName) {

    public static ProductInfo of(Product product, String creatorName) {
        return new ProductInfo(
                product.id().value(),
                product.description(),
                product.price().value(),
                creatorName
        );
    }
}
