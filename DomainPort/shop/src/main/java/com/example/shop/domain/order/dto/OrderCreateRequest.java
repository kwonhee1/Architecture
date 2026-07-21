package com.example.shop.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** order-01 : 주문 요청 (옵션 필수, 동일 optionId 는 개수를 합산) */
public record OrderCreateRequest(
        @NotEmpty(message = "주문할 옵션은 최소 1개 이상이어야 합니다.") @Valid List<OrderItemInfo> orderItems,
        Long couponId
) {}
