package com.example.shop.domain.order.dto;

/** order-01 요청/응답의 제품 주문 한 줄 { 옵션 id, 개수 } */
public record OrderItemResponse(
        Long optionId,
        int count
) {}
