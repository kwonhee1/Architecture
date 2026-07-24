package com.example.shop.user.domain.model.vo;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;

import java.util.regex.Pattern;

/**
 * 이메일 값 객체. 값에 정체성이 없고 형식 규칙을 스스로 검증한다.
 * (String email = X, Email email = O)
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
