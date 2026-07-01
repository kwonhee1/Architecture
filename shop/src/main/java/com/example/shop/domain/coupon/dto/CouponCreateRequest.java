package com.example.shop.domain.coupon.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CouponCreateRequest(
        @NotBlank String name,
        @Min(1) int discountAmount,
        @Min(0) int minOrderAmount,
        @NotNull @Future LocalDateTime expiresAt
) {}
