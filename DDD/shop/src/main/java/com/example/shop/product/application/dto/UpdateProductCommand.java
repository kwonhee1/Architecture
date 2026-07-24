package com.example.shop.product.application.dto;

/** product-02 : 수정할 값만 전달 (null 허용). */
public record UpdateProductCommand(String description, Long price) {
}
