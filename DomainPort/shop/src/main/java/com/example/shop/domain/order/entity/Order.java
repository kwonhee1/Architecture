package com.example.shop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * order 는 단독 domain 이므로 user / coupon 을 entity 로 참조하지 않고 Long id 로만 연결한다.
 * (OrderItem 은 같은 domain 이라 FK 로 직접 참조한다)
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주문자 (user domain) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 사용한 쿠폰 (user domain). 없으면 null */
    @Column(name = "coupon_id")
    private Long couponId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /** 쿠폰 적용 이후 최종 주문 금액 (>= 0) */
    @Column(nullable = false)
    private int amount;

    /** 주문 날짜 (date 까지만) */
    @Column(nullable = false)
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
