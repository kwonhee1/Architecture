package com.example.shop.order.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** DB 스키마 전용 엔티티 (주문 라인). 상품·옵션은 식별자 컬럼으로만 참조한다. */
@Entity
@Table(name = "order_lines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private long unitPrice;

    public OrderLineJpaEntity(Long id, Long productId, Long optionId, int quantity, long unitPrice) {
        this.id = id;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    void assignOrder(OrderJpaEntity order) {
        this.order = order;
    }
}
