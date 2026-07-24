package com.example.shop.product.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** DB 스키마 전용 엔티티. 소속 상품은 식별자 컬럼으로만 참조한다. */
@Entity
@Table(name = "product_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private long additionalPrice;

    @Column(nullable = false)
    private long stock;

    public OptionJpaEntity(Long id, Long productId, String description, long additionalPrice, long stock) {
        this.id = id;
        this.productId = productId;
        this.description = description;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }
}
