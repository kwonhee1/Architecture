package com.example.shop.domain.coupon.service;

import com.example.shop.domain.coupon.dto.CouponCreateRequest;
import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.repository.CouponRepository;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserService userService;

    @Transactional
    public CouponResponse create(CouponCreateRequest request) {
        Coupon coupon = Coupon.builder()
                .user(userService.getUser(request.userId()))
                .name(request.name())
                .discountAmount(request.discountAmount())
                .minOrderAmount(request.minOrderAmount())
                .expiresAt(request.expiresAt())
                .build();
        return CouponResponse.from(couponRepository.save(coupon));
    }

    public List<CouponResponse> getMyCoupons(Long userId) {
        return couponRepository.findByUserId(userId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    public Coupon getEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("쿠폰을 찾을 수 없습니다."));
    }
}
