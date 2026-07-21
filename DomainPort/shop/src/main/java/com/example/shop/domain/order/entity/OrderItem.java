package com.example.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 제품 주문.
 * order 와는 같은 domain 이라 FK 직접 참조, product / option 은 다른 domain 이라 Long id 로만 연결한다.
 * <p>
 * amount 는 주문 시점에 option 이 계산해 알려준 이 줄의 주문 금액이다. order 가 단가를 곱해
 * 만들지 않는다. (이후 상품 금액이 바뀌어도 주문 금액은 흔들리지 않는다)
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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** product domain */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** product domain */
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int amount;

    @Builder
    public OrderItem(Order order, Long productId, Long optionId, int quantity, int amount) {
        this.order = order;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.amount = amount;
    }
}
