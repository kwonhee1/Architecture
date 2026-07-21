package com.example.shop.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ── 공통 / 인증 ──
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // ── user ──
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_CHARGE_POINT(HttpStatus.BAD_REQUEST, "충전 포인트는 0보다 커야 합니다."),

    // ── coupon ──
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    NOT_COUPON_OWNER(HttpStatus.FORBIDDEN, "보유한 쿠폰이 아닙니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),

    // ── product ──
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    NOT_PRODUCT_OWNER(HttpStatus.FORBIDDEN, "상품 생성자만 수정할 수 있습니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "판매 금액은 0보다 커야 합니다."),
    PRICE_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "이미 주문이 존재하여 금액을 수정할 수 없습니다."),

    // ── option ──
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "옵션을 찾을 수 없습니다."),
    INVALID_OPTION_PRICE(HttpStatus.BAD_REQUEST, "추가 금액과 상품 금액의 합은 0보다 커야 합니다."),
    INVALID_OPTION_STOCK(HttpStatus.BAD_REQUEST, "재고 개수는 0 이상이어야 합니다."),
    ALREADY_HAS_ORDER(HttpStatus.FORBIDDEN, "이미 주문이 존재하여 처리할 수 없습니다."),

    // ── order ──
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    NOT_ORDER_OWNER(HttpStatus.FORBIDDEN, "내 주문이 아닙니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_ORDER_AMOUNT(HttpStatus.BAD_REQUEST, "주문 금액은 0보다 커야 합니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
