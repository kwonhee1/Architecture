package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.order.dto.OrderItemResponse;
import com.example.shop.domain.user.dto.response.Creator;

import java.time.LocalDate;
import java.util.List;

/** order-01 응답 (주문자 정보는 { name } 이라 Creator 를 그대로 쓴다) */
public record OrderResult(
        Long id,
        List<OrderItemResponse> productOrders,
        int amount,
        LocalDate orderDate,
        Creator orderer
) {}
