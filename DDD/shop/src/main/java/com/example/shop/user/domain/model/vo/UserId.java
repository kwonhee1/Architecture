package com.example.shop.user.domain.model.vo;

/**
 * user aggregate 식별자.
 * aggregate 간 참조는 객체가 아니라 식별자(ID)로 한다.
 */
public record UserId(Long value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId 는 null 일 수 없습니다.");
        }
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }
}
