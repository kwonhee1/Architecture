package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.coupon.dto.CouponResponse;
import com.example.shop.domain.order.entity.Order;

import java.time.LocalDate;
import java.util.List;

/** order-02 응답 (구매자 주문 리스트) */
public record BuyerOrderResponse(
        Long id,
        List<BuyerOrderLineDetail> productOrders,
        int amount,
        LocalDate orderDate,
        CouponResponse coupon
) {
    public static BuyerOrderResponse from(Order order) {
        List<BuyerOrderLineDetail> lines = order.getItems().stream()
                .map(BuyerOrderLineDetail::from)
                .toList();
        return new BuyerOrderResponse(
                order.getId(),
                lines,
                order.getAmount(),
                order.getOrderDate(),
                order.getCoupon() != null ? CouponResponse.from(order.getCoupon()) : null
        );
    }
}
