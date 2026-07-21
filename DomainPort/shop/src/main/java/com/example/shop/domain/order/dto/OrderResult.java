package com.example.shop.domain.order.dto;

import com.example.shop.domain.user.dto.CreatorResponse;

import java.time.LocalDate;
import java.util.List;

/** order-01 응답 (주문자 정보는 { name } 이라 user 의 CreatorResponse 를 그대로 쓴다) */
public record OrderResult(
        Long id,
        List<OrderItemInfo> productOrders,
        int amount,
        LocalDate orderDate,
        CreatorResponse orderer
) {}
