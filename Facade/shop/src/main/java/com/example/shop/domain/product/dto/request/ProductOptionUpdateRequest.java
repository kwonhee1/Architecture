package com.example.shop.domain.product.dto.request;

/** option-02 : 옵션 수정 요청 (수정할 값만 전달, null 허용) */
public record ProductOptionUpdateRequest(
        String description,
        Integer additionalPrice,
        Integer stock
) {}
