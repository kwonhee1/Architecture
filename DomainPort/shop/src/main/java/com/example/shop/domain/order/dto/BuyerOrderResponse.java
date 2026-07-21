package com.example.shop.domain.order.dto;

import com.example.shop.domain.user.dto.CouponResponse;

import java.time.LocalDate;
import java.util.List;

/** order-02 응답 (구매자 주문 리스트). 쿠폰은 coupon-02 응답과 같은 모양이라 그쪽 dto 를 쓴다. */
public record BuyerOrderResponse(
        Long id,
        List<BuyerOrderLineDetail> productOrders,
        int amount,
        LocalDate orderDate,
        CouponResponse coupon
) {}
