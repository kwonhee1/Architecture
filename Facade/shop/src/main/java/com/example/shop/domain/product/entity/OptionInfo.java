package com.example.shop.domain.product.entity;

/**
 * product domain 이 외부(facade)에 노출하는 옵션 VO.
 * 옵션 응답 { 옵션 id, 설명, 추가 금액, 재고 } 와 형태가 같아 응답으로 그대로 쓴다.
 */
public record OptionInfo(
        Long id,
        String description,
        int additionalPrice,
        int stock
) {
    public static OptionInfo from(ProductOption option) {
        return new OptionInfo(option.getId(), option.getDescription(),
                option.getAdditionalPrice(), option.getStock());
    }
}
