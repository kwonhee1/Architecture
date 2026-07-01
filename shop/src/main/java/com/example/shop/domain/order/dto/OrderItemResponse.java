package com.example.shop.domain.order.dto;

import com.example.shop.domain.order.entity.OrderItem;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Long optionId,
        String optionName,
        int quantity,
        int unitPrice,
        int subtotal
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getOption() != null ? item.getOption().getId() : null,
                item.getOption() != null ? item.getOption().getName() : null,
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
