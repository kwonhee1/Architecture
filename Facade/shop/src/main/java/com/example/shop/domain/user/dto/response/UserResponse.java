package com.example.shop.domain.user.dto.response;

import com.example.shop.domain.user.entity.User;

/** user-03 / user-04 응답 - password 는 반환하지 않는다 */
public record UserResponse(
        String name,
        int point
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getName(), user.getPoint());
    }
}
