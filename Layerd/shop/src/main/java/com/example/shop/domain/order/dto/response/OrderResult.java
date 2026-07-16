package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.order.dto.OrderItemInfo;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.product.dto.Creator;

import java.time.LocalDate;
import java.util.List;

/** order-01 응답 */
public record OrderResult(
        Long id,
        List<OrderItemInfo> productOrders,
        int amount,
        LocalDate orderDate,
        Creator creator
) {
    public static OrderResult from(Order order) {
        List<OrderItemInfo> lines = order.getItems().stream()
                .map(OrderItemInfo::from)
                .toList();
        return new OrderResult(
                order.getId(),
                lines,
                order.getAmount(),
                order.getOrderDate(),
                Creator.from(order.getUser())
        );
    }
}
