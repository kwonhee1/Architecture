package com.example.shop.domain.coupon.repository;

import com.example.shop.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    Optional<UserCoupon> findByIdAndUserId(Long id, Long userId);
}
