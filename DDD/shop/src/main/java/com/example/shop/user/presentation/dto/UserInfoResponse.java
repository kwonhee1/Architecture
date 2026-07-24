package com.example.shop.user.presentation.dto;

import com.example.shop.user.application.dto.UserInfo;

/** user-03 / user-04 응답 (UserInfo) - password 는 반환하지 않는다. */
public record UserInfoResponse(String name, long point) {

    public static UserInfoResponse from(UserInfo info) {
        return new UserInfoResponse(info.name(), info.point());
    }
}
