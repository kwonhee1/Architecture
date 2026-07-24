package com.example.shop.user.application.dto;

import com.example.shop.user.domain.model.User;

/**
 * user-03 / user-04 결과. presentation 으로 domain 객체가 아니라 이 결과 DTO 를 넘긴다.
 * password 는 담지 않는다.
 */
public record UserInfo(String name, long point) {

    public static UserInfo from(User user) {
        return new UserInfo(user.name(), user.point().value());
    }
}
