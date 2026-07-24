package com.example.shop.order.presentation.dto;

import com.example.shop.order.application.dto.BuyerOrderInfo;

import java.time.LocalDate;
import java.util.List;

/** order-02 응답 (구매자 주문 리스트). { 상품 정보, 옵션 정보, 개수 } · 쿠폰 정보 포함. */
public record BuyerOrderResponse(
        Long id,
        List<LineDetail> productOrders,
        long amount,
        LocalDate orderDate,
        Coupon coupon
) {
    public record LineDetail(Product product, Option option, int count) {
    }

    public record Product(Long id, String description, long price, Creator creator) {
    }

    public record Creator(String name) {
    }

    public record Option(Long id, String description, long additionalPrice, long stock) {
    }

    public record Coupon(Long id, String name, long discountAmount) {
    }

    public static BuyerOrderResponse from(BuyerOrderInfo info) {
        List<LineDetail> lines = info.productOrders().stream()
                .map(d -> new LineDetail(
                        new Product(d.product().id(), d.product().description(),
                                d.product().price(), new Creator(d.product().creatorName())),
                        new Option(d.option().id(), d.option().description(),
                                d.option().additionalPrice(), d.option().stock()),
                        d.count()))
                .toList();
        Coupon coupon = info.coupon() == null ? null
                : new Coupon(info.coupon().id(), info.coupon().name(), info.coupon().discountAmount());
        return new BuyerOrderResponse(info.id(), lines, info.amount(), info.orderDate(), coupon);
    }
}
