package com.example.shop.domain.coupon.controller;

import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.coupon.service.CouponService;
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

    private final CouponService couponService;

    /** coupon-02 : 쿠폰 리스트 조회 (사용 가능한 쿠폰만) */
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getMyCoupons(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(couponService.getMyCoupons(loginUserId));
    }
}
