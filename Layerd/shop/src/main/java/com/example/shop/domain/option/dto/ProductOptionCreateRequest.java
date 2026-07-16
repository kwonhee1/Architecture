package com.example.shop.domain.option.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** option-01 : 옵션 생성 요청 */
public record ProductOptionCreateRequest(
        @NotBlank String description,
        @NotNull Integer additionalPrice,
        @NotNull Integer stock
) {}
