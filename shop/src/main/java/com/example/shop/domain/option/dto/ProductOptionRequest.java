package com.example.shop.domain.option.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductOptionRequest(
        @NotBlank String name,
        @Min(0) int additionalPrice,
        @Min(0) int stock
) {}
