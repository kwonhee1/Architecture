package com.example.shop.order.presentation.dto;

import com.example.shop.order.application.dto.OrderLineCommand;
import com.example.shop.order.application.dto.PlaceOrderCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** order-01 : 주문 요청 (옵션 필수, 동일 optionId 는 개수 합산). */
public record OrderCreateRequest(
        @NotEmpty List<OrderItem> orderItems,
        Long couponId
) {
    public record OrderItem(@NotNull Long optionId, int count) {
    }

    public PlaceOrderCommand toCommand() {
        List<OrderLineCommand> items = orderItems.stream()
                .map(i -> new OrderLineCommand(i.optionId(), i.count()))
                .toList();
        return new PlaceOrderCommand(items, couponId);
    }
}
