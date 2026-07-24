package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.CreateProductCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** product-01 : 새 상품 등록 요청 (판매 금액 > 0) */
public record ProductCreateRequest(
        @NotBlank String description,
        @Positive(message = "판매 금액은 0보다 커야 합니다.") long price
) {
    public CreateProductCommand toCommand() {
        return new CreateProductCommand(description, price);
    }
}
