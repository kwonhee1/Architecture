package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.ProductInfo;

/** 상품 등록/수정/목록 응답 (옵션 미포함) */
public record ProductResponse(Long id, String description, long price, CreatorResponse creator) {

    public static ProductResponse from(ProductInfo info) {
        return new ProductResponse(
                info.id(),
                info.description(),
                info.price(),
                CreatorResponse.of(info.creatorName())
        );
    }
}
