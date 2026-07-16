package com.example.shop.domain.order.dto.response;

/** order-03 응답의 { 구매자 정보, 개수 } */
public record SellerOrderEntry(
        BuyerInfo buyer,
        int count
) {}
