package com.example.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * product / option 은 다른 domain 이므로 Long id 로만 참조한다.
 * order 는 같은 domain 이므로 직접 FK 로 참조한다.
 */
@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "option_id", nullable = false, updatable = false)
    private Long optionId;

    @Column(nullable = false)
    private int quantity;

    /** 이 줄의 구매 금액 (쿠폰 적용 전) */
    @Column(nullable = false)
    private int lineAmount;

    @Builder
    public OrderItem(Order order, Long productId, Long optionId, int quantity, int lineAmount) {
        this.order = order;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.lineAmount = lineAmount;
    }
}
