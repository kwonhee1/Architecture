package com.example.shop.coupon.presentation;

import com.example.shop.coupon.application.CouponQueryService;
import com.example.shop.coupon.presentation.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryService couponQueryService;

    /** coupon-02 : 쿠폰 리스트 조회 (사용 가능한 쿠폰만) */
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getMyCoupons(@RequestAttribute Long loginUserId) {
        List<CouponResponse> body = couponQueryService.getAvailableCoupons(loginUserId).stream()
                .map(CouponResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }
}
