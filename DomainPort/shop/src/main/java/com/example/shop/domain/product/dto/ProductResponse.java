package com.example.shop.domain.product.dto;

import com.example.shop.domain.product.service.vo.ProductVo.ProductInfo;
import com.example.shop.domain.user.dto.CreatorResponse;

/** product-01/02/03/05 응답 (옵션 정보 미포함) */
public record ProductResponse(
        Long id,
        String description,
        int price,
        CreatorResponse creator
) {
    /** port 가 내준 경계 VO 를 응답 모양으로 옮긴다 (order-02 의 상품 자리에서도 쓴다) */
    public static ProductResponse from(ProductInfo info) {
        return new ProductResponse(
                info.id(),
                info.description(),
                info.price(),
                new CreatorResponse(info.creatorName()));
    }
}
