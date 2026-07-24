package com.example.shop.product.presentation.dto;

import com.example.shop.product.application.dto.UpdateOptionCommand;

/** option-02 : 옵션 수정 요청 (수정할 값만 전달, null 허용) */
public record OptionUpdateRequest(String description, Long additionalPrice, Long stock) {
    public UpdateOptionCommand toCommand() {
        return new UpdateOptionCommand(description, additionalPrice, stock);
    }
}
