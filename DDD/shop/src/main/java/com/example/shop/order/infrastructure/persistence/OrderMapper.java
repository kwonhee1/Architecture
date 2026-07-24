package com.example.shop.order.infrastructure.persistence;

import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.OrderLine;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.model.vo.OrderId;

import java.util.List;

final class OrderMapper {

    private OrderMapper() {
    }

    static OrderJpaEntity toEntity(Order order) {
        Long id = order.id() == null ? null : order.id().value();
        OrderJpaEntity entity = new OrderJpaEntity(
                id, order.buyerId().value(), order.couponId(), order.amount(), order.orderDate());
        for (OrderLine line : order.lines()) {
            entity.addLine(new OrderLineJpaEntity(
                    line.id(), line.productId(), line.optionId(), line.quantity(), line.unitPrice()));
        }
        return entity;
    }

    static Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> lines = entity.getLines().stream()
                .map(l -> OrderLine.reconstitute(
                        l.getId(), l.getProductId(), l.getOptionId(), l.getQuantity(), l.getUnitPrice()))
                .toList();
        return Order.reconstitute(
                OrderId.of(entity.getId()),
                BuyerId.of(entity.getBuyerId()),
                entity.getCouponId(),
                lines,
                entity.getAmount(),
                entity.getOrderDate()
        );
    }
}
