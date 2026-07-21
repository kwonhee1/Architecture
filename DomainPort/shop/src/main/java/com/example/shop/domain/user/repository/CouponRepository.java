package com.example.shop.domain.user.repository;

import com.example.shop.domain.user.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    /** coupon-02 : 사용 가능한(미사용) 쿠폰만 조회 */
    List<Coupon> findByUserIdAndUsedFalse(Long userId);
}
