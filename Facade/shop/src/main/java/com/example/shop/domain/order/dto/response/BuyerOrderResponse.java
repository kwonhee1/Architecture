package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.user.entity.CouponInfo;

import java.time.LocalDate;
import java.util.List;

/** order-02 응답 (구매자 주문 리스트) */
public record BuyerOrderResponse(
        Long id,
        List<BuyerOrderLineDetail> productOrders,
        int amount,
        LocalDate orderDate,
        CouponInfo coupon
) {}
