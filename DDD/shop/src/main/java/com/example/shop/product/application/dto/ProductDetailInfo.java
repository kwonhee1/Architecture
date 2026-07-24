package com.example.shop.product.application.dto;

import java.util.List;

/** product-04 상세 { 상품 정보, 옵션 리스트 }. */
public record ProductDetailInfo(
        Long id,
        String description,
        long price,
        String creatorName,
        List<OptionInfo> options
) {
}
