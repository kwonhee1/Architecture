package com.example.shop.order.presentation.dto;

import com.example.shop.order.application.dto.SellerOrderInfo;

import java.util.List;

/** order-03 응답 (옵션별 [{ 구매자{email,name}, 개수 }]). */
public record SellerOrderResponse(long optionId, List<Entry> orders) {

    public record Entry(Buyer buyer, int count) {
    }

    public record Buyer(String email, String name) {
    }

    public static SellerOrderResponse from(SellerOrderInfo info) {
        List<Entry> entries = info.orders().stream()
                .map(e -> new Entry(new Buyer(e.buyer().email(), e.buyer().name()), e.count()))
                .toList();
        return new SellerOrderResponse(info.optionId(), entries);
    }
}
