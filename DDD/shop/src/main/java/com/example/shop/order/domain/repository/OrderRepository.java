package com.example.shop.order.domain.repository;

import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.model.vo.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    /** order-02 : 구매자의 주문 목록. */
    List<Order> findByBuyerId(BuyerId buyerId);

    /** order-03 : 특정 상품이 포함된 주문 목록. */
    List<Order> findByProductId(long productId);

    /** product-02 : 이 상품에 대한 주문 존재 여부. */
    boolean existsByProductId(long productId);

    /** option-02 : 이 옵션에 대한 주문 존재 여부. */
    boolean existsByOptionId(long optionId);

    void delete(Order order);
}
