package com.example.shop.domain.order.entity;

import com.example.shop.domain.order.dto.OrderItemResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * order domain 이 외부(facade)에 노출하는 제품 주문 한 줄 VO.
 * product / option 은 다른 domain 이므로 id 만 갖고 있다.
 */
@NoArgsConstructor
@Getter
public class OrderItemInfo {

    private Long productId;
    private Long optionId;
    private int count;

    public static OrderItemInfo from(OrderItem item) {
        OrderItemInfo info = new OrderItemInfo();

        info.productId = item.getProductId();
        info.optionId = item.getOptionId();
        info.count = item.getQuantity();

        return info;
    }

    public OrderItemResponse toOrderItemResponse() {
        return new OrderItemResponse(optionId, count);
    }
}
