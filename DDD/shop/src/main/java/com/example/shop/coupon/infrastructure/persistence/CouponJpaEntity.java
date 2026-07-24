package com.example.shop.coupon.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DB 스키마 전용 엔티티 (도메인 아님).
 * 소유자는 다른 aggregate(user) 이므로 연관관계 매핑이 아니라 식별자 컬럼으로만 참조한다.
 */
@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 100)
    private String name;

    /** 금액 할인 (% 아님), > 0 */
    @Column(nullable = false)
    private long discountAmount;

    @Column(nullable = false)
    private boolean used;

    public CouponJpaEntity(Long id, Long ownerId, String name, long discountAmount, boolean used) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.discountAmount = discountAmount;
        this.used = used;
    }
}
