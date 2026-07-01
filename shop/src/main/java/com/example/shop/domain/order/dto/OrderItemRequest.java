package com.example.shop.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull Long productId,
        Long optionId,
        @Min(1) int quantity
) {}
