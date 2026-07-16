package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.user.entity.User;

/** order-03 구매자 정보 { email, name } */
public record BuyerInfo(
        String email,
        String name
) {
    public static BuyerInfo from(User user) {
        return new BuyerInfo(user.getEmail(), user.getName());
    }
}
