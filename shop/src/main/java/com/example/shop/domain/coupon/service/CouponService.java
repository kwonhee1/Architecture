package com.example.shop.domain.coupon.service;

import com.example.shop.domain.coupon.dto.CouponCreateRequest;
import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.coupon.dto.UserCouponResponse;
import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.entity.UserCoupon;
import com.example.shop.domain.coupon.repository.CouponRepository;
import com.example.shop.domain.coupon.repository.UserCouponRepository;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    @Transactional
    public CouponResponse create(CouponCreateRequest request) {
        Coupon coupon = Coupon.builder()
                .name(request.name())
                .discountAmount(request.discountAmount())
                .minOrderAmount(request.minOrderAmount())
                .expiresAt(request.expiresAt())
                .build();
        return CouponResponse.from(couponRepository.save(coupon));
    }

    public List<CouponResponse> getAll() {
        return couponRepository.findByActiveTrue().stream()
                .map(CouponResponse::from)
                .toList();
    }

    @Transactional
    public UserCouponResponse issue(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("쿠폰을 찾을 수 없습니다."));
        if (!coupon.isActive() || coupon.isExpired()) {
            throw new IllegalStateException("발급 불가능한 쿠폰입니다.");
        }
        if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        UserCoupon userCoupon = UserCoupon.builder().user(user).coupon(coupon).build();
        return UserCouponResponse.from(userCouponRepository.save(userCoupon));
    }

    public List<UserCouponResponse> getMyCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId).stream()
                .map(UserCouponResponse::from)
                .toList();
    }

    public UserCoupon getEntityByIdAndUser(Long userCouponId, Long userId) {
        return userCouponRepository.findByIdAndUserId(userCouponId, userId)
                .orElseThrow(() -> new NoSuchElementException("보유한 쿠폰이 아닙니다."));
    }
}
