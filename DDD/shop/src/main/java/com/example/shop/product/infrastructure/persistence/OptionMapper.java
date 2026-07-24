package com.example.shop.product.infrastructure.persistence;

import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;

final class OptionMapper {

    private OptionMapper() {
    }

    static OptionJpaEntity toEntity(Option option) {
        Long id = option.id() == null ? null : option.id().value();
        return new OptionJpaEntity(id, option.productId().value(),
                option.description(), option.additionalPrice(), option.stock());
    }

    static Option toDomain(OptionJpaEntity entity) {
        return Option.reconstitute(
                OptionId.of(entity.getId()),
                ProductId.of(entity.getProductId()),
                entity.getDescription(),
                entity.getAdditionalPrice(),
                entity.getStock()
        );
    }
}
