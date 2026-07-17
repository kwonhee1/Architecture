package com.example.shop.domain.user.facade;

import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** coupon application service */
@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회 */
    @Transactional(readOnly = true)
    public List<CouponInfo> getMyCoupons(Long userId) {
        return couponService.getMyCoupons(userId);
    }
}
