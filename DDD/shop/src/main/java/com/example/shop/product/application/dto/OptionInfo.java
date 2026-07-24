package com.example.shop.product.application.dto;

import com.example.shop.product.domain.model.Option;

/** 옵션 정보 { 옵션 id, 설명, 추가 금액, 재고 }. */
public record OptionInfo(Long id, String description, long additionalPrice, long stock) {

    public static OptionInfo from(Option option) {
        return new OptionInfo(
                option.id().value(),
                option.description(),
                option.additionalPrice(),
                option.stock()
        );
    }
}
