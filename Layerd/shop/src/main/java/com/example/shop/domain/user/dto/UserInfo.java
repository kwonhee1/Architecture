package com.example.shop.domain.user.dto;

import com.example.shop.domain.user.entity.User;

/** user-03 / user-04 응답 (UserInfo) - password 는 반환하지 않음 */
public record UserInfo(
        String name,
        long point
) {
    public static UserInfo from(User user) {
        return new UserInfo(user.getName(), user.getPoint());
    }
}
