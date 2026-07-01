package com.example.shop.domain.order.service;

import com.example.shop.domain.coupon.entity.UserCoupon;
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
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductOptionService optionService;
    private final CouponService couponService;

    @Transactional
    public OrderResponse create(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        int totalAmount = 0;
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productService.getEntity(itemReq.productId());
            int unitPrice = product.getPrice();
            if (itemReq.optionId() != null) {
                ProductOption option = optionService.getEntity(itemReq.optionId());
                unitPrice += option.getAdditionalPrice();
            }
            totalAmount += unitPrice * itemReq.quantity();
        }

        UserCoupon userCoupon = null;
        int discountAmount = 0;
        if (request.userCouponId() != null) {
            userCoupon = couponService.getEntityByIdAndUser(request.userCouponId(), userId);
            if (userCoupon.getCoupon().isExpired() || !userCoupon.getCoupon().isActive()) {
                throw new IllegalStateException("사용 불가능한 쿠폰입니다.");
            }
            if (totalAmount < userCoupon.getCoupon().getMinOrderAmount()) {
                throw new IllegalStateException("최소 주문 금액을 충족하지 않아 쿠폰을 사용할 수 없습니다.");
            }
            discountAmount = userCoupon.getCoupon().getDiscountAmount();
            userCoupon.use();
        }

        Order order = Order.builder()
                .user(user)
                .userCoupon(userCoupon)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .build();
        orderRepository.save(order);

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productService.getEntity(itemReq.productId());
            ProductOption option = null;
            int unitPrice = product.getPrice();
            if (itemReq.optionId() != null) {
                option = optionService.getEntity(itemReq.optionId());
                option.decreaseStock(itemReq.quantity());
                unitPrice += option.getAdditionalPrice();
            }
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .option(option)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .build();
            order.getItems().add(item);
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
