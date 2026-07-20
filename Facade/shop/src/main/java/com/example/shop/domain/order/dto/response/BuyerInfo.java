package com.example.shop.domain.order.dto.response;

/** order-03 구매자 정보 { email, name } */
public record BuyerInfo(
        String email,
        String name
) {}
