package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.CreateOptionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** option-01 : 옵션 생성 요청 */
public record OptionCreateRequest(
        @NotBlank String description,
        @NotNull Long additionalPrice,
        @NotNull Long stock
) {
    public CreateOptionCommand toCommand() {
        return new CreateOptionCommand(description, additionalPrice, stock);
    }
}
