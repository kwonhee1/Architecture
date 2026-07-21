package com.example.shop.domain.product.dto;

import com.example.shop.domain.product.entity.ProductOption;
import com.example.shop.domain.product.service.vo.ProductOptionVo.OptionInfo;

/** option-01/02 응답, product-04 의 options, order-02 의 옵션 자리 */
public record ProductOptionResponse(
        Long id,
        String description,
        int additionalPrice,
        int stock
) {
    /** 같은 domain 안에서는 entity 를 그대로 옮긴다 */
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getDescription(),
                option.getAdditionalPrice(),
                option.getStock());
    }

    /** port 가 내준 경계 VO 를 응답 모양으로 옮긴다 (order-02 용) */
    public static ProductOptionResponse from(OptionInfo info) {
        return new ProductOptionResponse(
                info.id(),
                info.description(),
                info.additionalPrice(),
                info.stock());
    }
}
