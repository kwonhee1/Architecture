package com.example.shop.order.application.dto;

import java.util.List;

/** order-01 : 주문 요청 (동일 optionId 는 개수를 합산). */
public record PlaceOrderCommand(List<OrderLineCommand> items, Long couponId) {
}
