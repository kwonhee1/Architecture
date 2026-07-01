package com.example.shop.domain.coupon.controller;

import com.example.shop.domain.coupon.dto.CouponCreateRequest;
import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.coupon.dto.UserCouponResponse;
import com.example.shop.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/api/coupons")
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CouponCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.create(request));
    }

    @GetMapping("/api/coupons")
    public ResponseEntity<List<CouponResponse>> getAll() {
        return ResponseEntity.ok(couponService.getAll());
    }

    @PostMapping("/api/coupons/{couponId}/issue")
    public ResponseEntity<UserCouponResponse> issue(@PathVariable Long couponId,
                                                    @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.issue(couponId, loginUserId));
    }

    @GetMapping("/api/users/me/coupons")
    public ResponseEntity<List<UserCouponResponse>> getMyCoupons(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(couponService.getMyCoupons(loginUserId));
    }
}
