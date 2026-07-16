package com.example.shop.domain.option.dto;

import com.example.shop.domain.option.entity.ProductOption;

/** 옵션 정보 { 옵션 id, 설명, 추가 금액, 재고 } */
public record ProductOptionResponse(
        Long id,
        String description,
        int additionalPrice,
        int stock
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getDescription(),
                option.getAdditionalPrice(),
                option.getStock()
        );
    }
}
