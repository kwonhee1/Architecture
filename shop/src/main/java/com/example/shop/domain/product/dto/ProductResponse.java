package com.example.shop.domain.product.dto;

import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        Long creatorId,
        String creatorName,
        String name,
        int price,
        String description,
        ProductStatus status,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCreator().getId(),
                product.getCreator().getName(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}
