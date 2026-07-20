package com.example.shop.domain.user.dto.request;

import jakarta.validation.constraints.Positive;

/** user-04 : 포인트 충전 요청 (충전 포인트 > 0) */
public record ChargePointRequest(
        @Positive(message = "충전 포인트는 0보다 커야 합니다.") int point
) {}
