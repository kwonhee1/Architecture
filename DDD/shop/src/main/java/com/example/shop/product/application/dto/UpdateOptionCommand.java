package com.example.shop.product.application.dto;

/** option-02 : 수정할 값만 전달 (null 허용). */
public record UpdateOptionCommand(String description, Long additionalPrice, Long stock) {
}
