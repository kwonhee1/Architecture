package com.example.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * user / coupon 은 다른 domain 이므로 Long id 로만 참조한다.
 * (같은 domain 인 OrderItem 만 직접 FK 로 연결된다)
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    /** 쿠폰을 쓰지 않은 주문이면 null */
    @Column(name = "coupon_id")
    private Long couponId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /** 쿠폰 적용 이후 최종 주문 금액 */
    @Column(nullable = false)
    private int amount;

    /** 주문 날짜 (date 까지만) */
    @Column(nullable = false, updatable = false)
    private LocalDate orderDate;

    @Builder
    public Order(Long userId, Long couponId, int amount) {
        this.userId = userId;
        this.couponId = couponId;
        this.amount = amount;
        this.orderDate = LocalDate.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
