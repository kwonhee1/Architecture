package com.example.shop.order.presentation.dto;

import com.example.shop.order.application.dto.OrderInfo;

import java.time.LocalDate;
import java.util.List;

/** order-01 응답 { 주문 id, 제품 주문[{옵션 id, 개수}], 주문 금액, 주문 날짜, 주문자 }. */
public record OrderResponse(
        Long id,
        List<Line> productOrders,
        long amount,
        LocalDate orderDate,
        Orderer orderer
) {
    public record Line(long optionId, int count) {
    }

    public record Orderer(String name) {
    }

    public static OrderResponse from(OrderInfo info) {
        List<Line> lines = info.productOrders().stream()
                .map(l -> new Line(l.optionId(), l.count()))
                .toList();
        return new OrderResponse(info.id(), lines, info.amount(),
                info.orderDate(), new Orderer(info.ordererName()));
    }
}
