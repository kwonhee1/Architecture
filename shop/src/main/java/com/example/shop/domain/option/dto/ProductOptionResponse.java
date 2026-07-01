package com.example.shop.domain.option.dto;

import com.example.shop.domain.option.entity.ProductOption;

public record ProductOptionResponse(
        Long id,
        Long productId,
        String name,
        int additionalPrice,
        int stock
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getProduct().getId(),
                option.getName(),
                option.getAdditionalPrice(),
                option.getStock()
        );
    }
}
