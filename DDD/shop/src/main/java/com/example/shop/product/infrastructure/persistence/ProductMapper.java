package com.example.shop.product.infrastructure.persistence;

import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.Money;
import com.example.shop.product.domain.model.vo.ProductId;

final class ProductMapper {

    private ProductMapper() {
    }

    static ProductJpaEntity toEntity(Product product) {
        Long id = product.id() == null ? null : product.id().value();
        return new ProductJpaEntity(id, product.creatorId().value(),
                product.description(), product.price().value());
    }

    static Product toDomain(ProductJpaEntity entity) {
        return Product.reconstitute(
                ProductId.of(entity.getId()),
                CreatorId.of(entity.getCreatorId()),
                entity.getDescription(),
                Money.of(entity.getPrice())
        );
    }
}
