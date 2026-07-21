package com.example.shop.domain.user.controller;

import com.example.shop.domain.user.service.usecase.CouponUseCase;
import com.example.shop.domain.user.dto.CouponResponse;
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

    private final CouponUseCase couponUseCase;

    /** coupon-02 : 쿠폰 리스트 조회 (사용 가능한 쿠폰만) */
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getMyCoupons(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(couponUseCase.getMyCoupons(loginUserId));
    }
}
