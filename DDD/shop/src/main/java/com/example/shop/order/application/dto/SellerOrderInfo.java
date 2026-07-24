package com.example.shop.order.application.dto;

import java.util.List;

/** order-03 응답 (옵션 id 기준으로 묶인 구매자/개수 리스트). */
public record SellerOrderInfo(long optionId, List<Entry> orders) {

    public record Entry(BuyerView buyer, int count) {
    }

    public record BuyerView(String email, String name) {
    }
}
