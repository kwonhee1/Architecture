package com.example.shop.order.application.dto;

import java.time.LocalDate;
import java.util.List;

/** order-01 응답 { 주문 id, 제품 주문[{옵션 id, 개수}], 주문 금액, 주문 날짜, 주문자 이름 }. */
public record OrderInfo(
        Long id,
        List<Line> productOrders,
        long amount,
        LocalDate orderDate,
        String ordererName
) {
    public record Line(long optionId, int count) {
    }
}
