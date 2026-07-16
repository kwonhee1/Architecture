package com.example.shop.domain.coupon.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.repository.CouponRepository;
import com.example.shop.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private static final String SIGNUP_COUPON_NAME = "회원 가입 쿠폰";
    private static final int SIGNUP_COUPON_AMOUNT = 1000;

    private final CouponRepository couponRepository;

    /** coupon-01 : 회원 가입 시 자동으로 발급되는 쿠폰 (별도 API 없음) */
    @Transactional
    public void issueSignupCoupon(User user) {
        Coupon coupon = Coupon.builder()
                .user(user)
                .name(SIGNUP_COUPON_NAME)
                .discountAmount(SIGNUP_COUPON_AMOUNT)
                .build();
        couponRepository.save(coupon);
    }

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회 */
    public List<CouponResponse> getMyCoupons(Long userId) {
        return couponRepository.findByUserIdAndUsedFalse(userId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    public Coupon getEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
    }

}
