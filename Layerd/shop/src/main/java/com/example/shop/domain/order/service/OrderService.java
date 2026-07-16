package com.example.shop.domain.order.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.coupon.entity.Coupon;
import com.example.shop.domain.coupon.service.CouponService;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.service.ProductOptionService;
import com.example.shop.domain.order.dto.*;
import com.example.shop.domain.order.dto.request.OrderCreateRequest;
import com.example.shop.domain.order.dto.response.*;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.repository.OrderItemRepository;
import com.example.shop.domain.order.repository.OrderRepository;
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
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ProductOptionService optionService;
    private final CouponService couponService;

    /**
     * order-01 : 주문.
     * 주문 생성 / 쿠폰 사용 / 포인트 차감 / 재고 차감을 한 트랜잭션에서 처리한다.
     * 재고 부족·포인트 부족·주문 금액 <= 0 등 실패 시 예외로 전체 롤백(취소)된다.
     */
    @Transactional
    public OrderResult create(Long userId, OrderCreateRequest request) {
        User user = userService.getUser(userId);

        // 쿠폰 사용 (선택)
        Coupon coupon = null;
        int discount = 0;
        if (request.couponId() != null) {
            coupon = couponService.getEntity(request.couponId());
            discount = coupon.use(user); // 소유/사용여부 검증 → 예외 시 롤백
        }

        Order order = Order.builder()
                .user(user)
                .coupon(coupon)
                .amount(0)
                .build();

        // 동일 optionId 는 개수를 합산 (입력 순서 유지)
        Map<Long, OrderItemInfo> itemRequestMap = new HashMap<>();
        for(OrderItemInfo itemRequest : request.orderItems()) {
            if(itemRequestMap.containsKey(itemRequest.optionId()))
                itemRequestMap.merge( // 이전값과 count 합산
                        itemRequest.optionId(),
                        itemRequest,
                        (a,b)-> new OrderItemInfo(itemRequest.optionId(), a.count() + b.count())
                );
            else
                itemRequestMap.put(itemRequest.optionId(), itemRequest);
        }

        int totalAmount = 0;
        for(OrderItemInfo itemRequest : itemRequestMap.values()) {
            ProductOption option = optionService.getEntityWithProduct(itemRequest.optionId());
            Product product = option.getProduct();
            option.decreaseStock(itemRequest.count());
            totalAmount += (option.getAdditionalPrice() + product.getPrice()) * itemRequest.count();
            order.addItem(
                    OrderItem.builder()
                        .order(order)
                        .product(product)
                        .option(option)
                        .quantity(itemRequest.count())
                        .build()
            );
        }

        int finalAmount = totalAmount - discount;
        if (finalAmount <= 0)
            finalAmount = 0;

        order.setAmount(finalAmount);
        order = orderRepository.save(order);

        user.usePoint(finalAmount); // 포인트 부족 시 예외 → 롤백

        return OrderResult.from(order);
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    public List<BuyerOrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(BuyerOrderResponse::from)
                .toList();
    }

    /** order-03 : 판매자 주문 리스트 조회 (상품 생성자만, 옵션별로 묶어서 반환) */
    public List<SellerOptionOrders> getProductOrders(Long productId, Long userId) {
        Product product = productService.getProduct(productId);
        product.validateOwner(userId); // 판매자(생성자)만 조회 가능

        Map<ProductOption, List<SellerOrderEntry>> grouped = new LinkedHashMap<>();
        for (OrderItem item : orderItemRepository.findByProductId(productId)) {
            grouped.computeIfAbsent(item.getOption(), k -> new ArrayList<>())
                    .add(new SellerOrderEntry(BuyerInfo.from(item.getOrder().getUser()), item.getQuantity()));
        }
        return grouped.entrySet().stream()
                .map(e -> new SellerOptionOrders(e.getKey().getId(), e.getValue()))
                .toList();
    }

    /**
     * order-04 : 주문 취소.
     * 포인트 / 쿠폰 / 재고를 되돌리고 order 를 삭제한다. 내가 주문한 주문만 취소 가능.
     */
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.NOT_ORDER_OWNER);
        }

        for (OrderItem item : order.getItems()) {
            item.getOption().increaseStock(item.getQuantity());
        }
        if (order.getCoupon() != null) {
            order.getCoupon().restore();
        }
        User user = order.getUser();
        user.refundPoint(order.getAmount());

        orderRepository.delete(order);
    }

}
