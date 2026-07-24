package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.UpdateProductCommand;

/** product-02 : 상품 정보 수정 요청 (수정할 값만 전달, null 허용) */
public record ProductUpdateRequest(String description, Long price) {
    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(description, price);
    }
}
