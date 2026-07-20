package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.dto.response.ProductResponse;

/** order-02 응답의 제품 주문 상세 { 상품 정보, 옵션 정보, 개수 } */
public record BuyerOrderLineDetail(
        ProductResponse product,
        OptionInfo option,
        int count
) {}
