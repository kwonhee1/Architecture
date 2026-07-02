package com.example.shop.domain.coupon.entity;

import com.example.shop.domain.product.PurchaseResult;
import com.example.shop.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minOrderAmount;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    private LocalDateTime usedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
        this.used = false;
    }

    public int applyTo(
            List<PurchaseResult> orderList, // 구매 상품 내용 모두 : domain은 지금 필요한 값이 아닌, 개념적으로 필요한 모든 값을 요구할 필요가 있다
            User user
    ) {
        if (!this.user.getId().equals(user.getId())) {
            throw new IllegalStateException("보유한 쿠폰이 아닙니다.");
        }
        if (this.used) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        if (LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        int orderPrice = orderList.stream().mapToInt(PurchaseResult::getPurchasePrice).sum();
        if (orderPrice < this.minOrderAmount) {
            throw new IllegalStateException("최소 주문 금액을 충족하지 않아 쿠폰을 사용할 수 없습니다.");
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
        return this.discountAmount;
    }

    @Builder
    public Coupon(User user, String name, int discountAmount, int minOrderAmount, LocalDateTime expiresAt) {
        this.user = user;
        this.name = name;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.expiresAt = expiresAt;
    }
}
