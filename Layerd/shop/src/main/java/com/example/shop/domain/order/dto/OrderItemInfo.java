package com.example.shop.domain.order.dto;

import com.example.shop.domain.order.entity.OrderItem;

/** order-01 응답의 제품 주문 한 줄 { 옵션 id, 개수 } */
public record OrderItemInfo(
        Long optionId,
        int count
) {
    public static OrderItemInfo from(OrderItem item) {
        return new OrderItemInfo(item.getOption().getId(), item.getQuantity());
    }
}
