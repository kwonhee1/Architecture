package com.example.shop.domain.coupon.repository;

import com.example.shop.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByUserId(Long userId);
}
