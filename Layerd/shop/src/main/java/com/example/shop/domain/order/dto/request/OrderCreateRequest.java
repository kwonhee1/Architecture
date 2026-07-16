package com.example.shop.domain.order.dto.request;

import com.example.shop.domain.order.dto.OrderItemInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** order-01 : 주문 요청 (동일 optionId 는 개수를 합산) */
public record OrderCreateRequest(
        @NotEmpty @Valid List<OrderItemInfo> orderItems,
        Long couponId
) {}
