package com.example.shop.domain.user.dto.response;

import com.example.shop.domain.user.entity.User;

/**
 * user domain 이 외부(facade)에 노출하는 조회용 VO.
 * User entity 를 그대로 내보내지 않기 위한 타입이다.
 */
public record UserSummary(
        Long id,
        String email,
        String name
) {
    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getEmail(), user.getName());
    }
}
