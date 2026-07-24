package com.example.shop.user.domain.model.vo;

/**
 * 비밀번호 값 객체.
 * (명세) 인코딩하지 않고 저장/비교하며, 어떤 응답에도 노출하지 않는다.
 * 원문을 밖으로 꺼내는 getter 를 열지 않고, 일치 여부만 판단한다.
 */
public record Password(String value) {

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    public static Password of(String value) {
        return new Password(value);
    }

    public boolean matches(Password raw) {
        return this.value.equals(raw.value);
    }
}
