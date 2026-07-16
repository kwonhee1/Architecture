package com.example.shop.domain.product.dto;

import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.product.entity.Product;

import java.util.List;

/** product-04 상세 응답 (옵션 리스트 포함) */
public record ProductDetailResponse(
        Long id,
        String description,
        int price,
        Creator creator,
        List<ProductOptionResponse> options
) {
    public static ProductDetailResponse of(Product product, List<ProductOption> options) {
        return new ProductDetailResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                Creator.from(product.getCreator()),
                options.stream().map(ProductOptionResponse::from).toList()
        );
    }
}
