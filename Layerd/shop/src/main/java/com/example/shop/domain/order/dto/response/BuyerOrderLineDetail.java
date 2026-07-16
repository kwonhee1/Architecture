package com.example.shop.domain.order.dto.response;

import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.product.dto.ProductResponse;

/** order-02 응답의 제품 주문 상세 { 상품 정보, 옵션 정보, 개수 } */
public record BuyerOrderLineDetail(
        ProductResponse product,
        ProductOptionResponse option,
        int count
) {
    public static BuyerOrderLineDetail from(OrderItem item) {
        return new BuyerOrderLineDetail(
                ProductResponse.from(item.getProduct()),
                ProductOptionResponse.from(item.getOption()),
                item.getQuantity()
        );
    }
}
