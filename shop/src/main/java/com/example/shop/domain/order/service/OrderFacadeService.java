package com.example.shop.domain.order.service;

import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.service.CouponService;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.service.ProductOptionService;
import com.example.shop.domain.order.dto.OrderCreateRequest;
import com.example.shop.domain.order.dto.OrderItemRequest;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.product.PurchaseResult;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final OrderService orderService;
    private final ProductService productService;
    private final CouponService couponService;
    private final UserService userService;

    @Transactional
    public Order create(Long userId, OrderCreateRequest request) {
        User user = userService.getUser(userId);

        // product 차감
        List<PurchaseResult> purchaseResults = productService.purchase(request.items());

        // coupon 사용
        Coupon coupon = couponService.getEntity(request.couponId());
        int discountAmount = coupon != null ? coupon.applyTo(purchaseResults, user) : 0;

        // 가격 게산 --> domain 로직일까? 아님 단순 mapping 일 뿐임 / facade는 각 domain에 맞게 dto, request 값을 mapping할 책임을 가진다
        int totalPrice = purchaseResults
                .stream()
                .mapToInt(PurchaseResult::getPurchasePrice)
                .sum() - discountAmount;

        // order 생성
        return orderService.create(user, coupon, purchaseResults, totalPrice);
    }

}
