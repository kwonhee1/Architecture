package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.ProductDetailInfo;

import java.util.List;

/** product-04 상세 응답 (옵션 리스트 포함) */
public record ProductDetailResponse(
        Long id,
        String description,
        long price,
        CreatorResponse creator,
        List<OptionResponse> options
) {
    public static ProductDetailResponse from(ProductDetailInfo info) {
        return new ProductDetailResponse(
                info.id(),
                info.description(),
                info.price(),
                CreatorResponse.of(info.creatorName()),
                info.options().stream().map(OptionResponse::from).toList()
        );
    }
}
