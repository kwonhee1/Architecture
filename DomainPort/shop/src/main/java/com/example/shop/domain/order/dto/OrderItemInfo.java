package com.example.shop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** 제품 주문 한 줄 { 옵션 id, 개수 } - order-01 요청/응답 공용 */
public record OrderItemInfo(
        @NotNull Long optionId,
        @Positive int count
) {}
