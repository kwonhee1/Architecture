package com.example.shop.domain.order.dto;

import java.util.List;

/** order-03 응답 (옵션 id 기준으로 묶인 구매자/개수 리스트) */
public record SellerOptionOrders(
        Long optionId,
        List<SellerOrderEntry> orders
) {}
