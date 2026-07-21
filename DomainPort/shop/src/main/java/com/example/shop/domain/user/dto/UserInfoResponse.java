package com.example.shop.domain.user.dto;

import com.example.shop.domain.user.entity.User;

/** user-03 / user-04 응답 - password 는 반환하지 않는다 */
public record UserInfoResponse(
        String name,
        long point
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(user.getName(), user.getPoint());
    }
}
