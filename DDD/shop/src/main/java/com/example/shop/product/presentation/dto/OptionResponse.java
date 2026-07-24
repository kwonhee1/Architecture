package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.OptionInfo;

/** 옵션 정보 { 옵션 id, 설명, 추가 금액, 재고 } */
public record OptionResponse(Long id, String description, long additionalPrice, long stock) {

    public static OptionResponse from(OptionInfo info) {
        return new OptionResponse(
                info.id(),
                info.description(),
                info.additionalPrice(),
                info.stock()
        );
    }
}
