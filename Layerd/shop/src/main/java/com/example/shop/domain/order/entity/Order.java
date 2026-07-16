package com.example.shop.domain.order.entity;

import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /** 쿠폰 적용 이후 최종 주문 금액 */
    @Column(nullable = false)
    private int amount;

    /** 주문 날짜 (date 까지만) */
    @Column(nullable = false)
    private LocalDate orderDate;

    @Builder
    public Order(User user, Coupon coupon, int amount) {
        this.user = user;
        this.coupon = coupon;
        this.amount = amount;
        this.orderDate = LocalDate.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }
}
