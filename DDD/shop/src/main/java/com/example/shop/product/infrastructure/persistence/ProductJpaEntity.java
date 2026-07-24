package com.example.shop.product.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** DB 스키마 전용 엔티티. 생성자(user)는 식별자 컬럼으로만 참조한다. */
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false, updatable = false)
    private Long creatorId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private long price;

    public ProductJpaEntity(Long id, Long creatorId, String description, long price) {
        this.id = id;
        this.creatorId = creatorId;
        this.description = description;
        this.price = price;
    }
}
