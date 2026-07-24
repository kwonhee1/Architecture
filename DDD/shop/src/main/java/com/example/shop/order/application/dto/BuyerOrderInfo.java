package com.example.shop.order.application.dto;

import java.time.LocalDate;
import java.util.List;

/** order-02 응답 (구매자 주문 리스트). 다른 aggregate 데이터는 읽어서 view 로 담는다. */
public record BuyerOrderInfo(
        Long id,
        List<LineDetail> productOrders,
        long amount,
        LocalDate orderDate,
        CouponView coupon
) {
    public record LineDetail(ProductView product, OptionView option, int count) {
    }

    public record ProductView(Long id, String description, long price, String creatorName) {
    }

    public record OptionView(Long id, String description, long additionalPrice, long stock) {
    }

    public record CouponView(Long id, String name, long discountAmount) {
    }
}
