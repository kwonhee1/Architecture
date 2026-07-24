package com.example.shop.coupon.application;

import com.example.shop.coupon.application.dto.CouponInfo;
import com.example.shop.coupon.domain.model.vo.OwnerId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** coupon 조회 use case. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryService {

    private final CouponRepository couponRepository;

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회. */
    public List<CouponInfo> getAvailableCoupons(long userId) {
        return couponRepository.findAvailableByOwner(OwnerId.of(userId)).stream()
                .map(CouponInfo::from)
                .toList();
    }
}
