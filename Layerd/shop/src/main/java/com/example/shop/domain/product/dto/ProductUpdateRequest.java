package com.example.shop.domain.product.dto;

/** product-02 : 상품 정보 수정 요청 (수정할 값만 전달, null 허용) */
public record ProductUpdateRequest(
        String description,
        Integer price
) {}
