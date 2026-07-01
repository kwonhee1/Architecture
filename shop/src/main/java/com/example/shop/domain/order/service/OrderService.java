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
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ProductOptionService optionService;
    private final CouponService couponService;

    @Transactional
    public OrderResponse create(Long userId, OrderCreateRequest request) {
        int totalAmount = 0;
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productService.getProduct(itemReq.productId());
            int unitPrice = product.getPrice();
            if (itemReq.optionId() != null) {
                unitPrice += optionService.getEntity(itemReq.optionId()).getAdditionalPrice();
            }
            totalAmount += unitPrice * itemReq.quantity();
        }

        Coupon coupon = null;
        int discountAmount = 0;
        if (request.couponId() != null) {
            coupon = couponService.getEntityByIdAndUser(request.couponId(), userId);
            if (coupon.isExpired()) {
                throw new IllegalStateException("만료된 쿠폰입니다.");
            }
            if (totalAmount < coupon.getMinOrderAmount()) {
                throw new IllegalStateException("최소 주문 금액을 충족하지 않아 쿠폰을 사용할 수 없습니다.");
            }
            discountAmount = coupon.getDiscountAmount();
            coupon.use();
        }

        Order order = Order.builder()
                .user(userService.getUser(userId))
                .coupon(coupon)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .build();
        orderRepository.save(order);

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productService.getProduct(itemReq.productId());
            ProductOption option = null;
            int unitPrice = product.getPrice();
            if (itemReq.optionId() != null) {
                option = optionService.getEntity(itemReq.optionId());
                option.decreaseStock(itemReq.quantity());
                unitPrice += option.getAdditionalPrice();
            }
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .option(option)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .build());
        }

        return OrderResponse.from(order);
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
        order.cancel();
        return OrderResponse.from(order);
    }
}
