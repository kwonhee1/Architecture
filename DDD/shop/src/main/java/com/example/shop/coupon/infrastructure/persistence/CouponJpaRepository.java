package com.example.shop.coupon.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    /** 사용 가능한(미사용) 쿠폰만 조회 */
    List<CouponJpaEntity> findByOwnerIdAndUsedFalse(Long ownerId);
}
