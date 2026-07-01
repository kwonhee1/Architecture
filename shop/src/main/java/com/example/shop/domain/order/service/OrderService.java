package com.example.shop.domain.order.service;

import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.service.CouponService;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.service.ProductOptionService;
import com.example.shop.domain.order.dto.OrderCreateRequest;
import com.example.shop.domain.order.dto.OrderItemRequest;
import com.example.shop.domain.order.dto.OrderResponse;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.product.PurchaseResult;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ProductOptionService optionService;
    private final CouponService couponService;

    @Transactional
    public Order create(Long userId, OrderCreateRequest request) {
        List<PurchaseResult> purchaseResults = new ArrayList<>();
        int totalPurchasePrice = 0;

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productService.getProduct(itemReq.productId());
            ProductOption option = optionService.getEntity(itemReq.optionId());
            PurchaseResult purchaseResult = productService.purchase(product, option, itemReq.quantity());
            purchaseResults.add(purchaseResult);
            totalPurchasePrice += purchaseResult.getPurchasePrice();
        }

        Coupon coupon = couponService.getEntity(request.couponId());
        int discountAmount = coupon != null ? coupon.applyTo(totalPurchasePrice, userService.getUser(userId)) : 0;

        Order order = Order.builder()
                .user(userService.getUser(userId))
                .coupon(coupon)
                .totalAmount(totalPurchasePrice)
                .discountAmount(discountAmount)
                .build();

        for(PurchaseResult purchase : purchaseResults) {
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
