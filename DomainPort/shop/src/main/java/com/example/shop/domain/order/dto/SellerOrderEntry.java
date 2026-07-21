package com.example.shop.domain.order.dto;

/** order-03 응답의 { 구매자 정보, 개수 } */
public record SellerOrderEntry(
        BuyerResponse buyer,
        int count
) {}
