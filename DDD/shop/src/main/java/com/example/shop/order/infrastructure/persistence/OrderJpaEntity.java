package com.example.shop.order.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** DB 스키마 전용 엔티티. 회원·쿠폰은 식별자 컬럼으로만 참조한다. */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "coupon_id")
    private Long couponId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineJpaEntity> lines = new ArrayList<>();

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private LocalDate orderDate;

    public OrderJpaEntity(Long id, Long buyerId, Long couponId, long amount, LocalDate orderDate) {
        this.id = id;
        this.buyerId = buyerId;
        this.couponId = couponId;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    public void addLine(OrderLineJpaEntity line) {
        line.assignOrder(this);
        this.lines.add(line);
    }
}
