package com.example.shop.domain.order.service;

import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.order.dto.OrderResponse;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.product.PurchaseResult;
import com.example.shop.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(User user, Coupon coupon, List<PurchaseResult> purchaseList, int totalPrice) {
        Order order = Order.builder()
                .user(user)
                .coupon(coupon)
                .totalAmount(totalPrice)
                .build();

        for(PurchaseResult purchase : purchaseList) {
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(purchase.getProduct())
                    .option(purchase.getOption())
                    .quantity(purchase.getCount())
                    .unitPrice(purchase.getPurchasePrice())
                    .build());
        }
        return orderRepository.save(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOne(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancel(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다."));
        order.cancel(); // coupon roll back은?
        return OrderResponse.from(order);
    }
}
