package com.example.shop.domain.order.dto;

import com.example.shop.domain.product.dto.ProductOptionResponse;
import com.example.shop.domain.product.dto.ProductResponse;

/**
 * order-02 응답의 제품 주문 상세 { 상품 정보, 옵션 정보, 개수 }.
 * <p>
 * 상품·옵션 정보는 product-03 / option-01 응답과 같은 모양이라 그쪽 dto 를 그대로 쓴다.
 * (같은 모양의 dto 를 order 쪽에 다시 만들지 않는다)
 */
public record BuyerOrderLineDetail(
        ProductResponse product,
        ProductOptionResponse option,
        int count
) {}
