package com.example.shop.common.exception;

/** 실패 응답 공통 포맷 (shopping-api.yaml 의 Error 스키마) */
public record ErrorResponse(String message) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
