package com.example.shop.domain.order.facade;

import com.example.shop.domain.order.dto.*;
import com.example.shop.domain.order.dto.request.OrderCreateRequest;
import com.example.shop.domain.order.dto.response.*;
import com.example.shop.domain.order.entity.OrderInfo;
import com.example.shop.domain.order.entity.OrderItemInfo;
import com.example.shop.domain.order.service.OrderService;
import com.example.shop.domain.product.dto.*;
import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.product.service.ProductOptionService;
import com.example.shop.domain.product.service.ProductService;
import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.entity.UserInfo;
import com.example.shop.domain.user.service.CouponService;
import com.example.shop.domain.user.service.UserService;
import com.example.shop.domain.user.service.vo.CouponResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * order application service.
 * 주문은 재고(product) · 쿠폰/포인트(user) · 주문(order) 이 함께 움직이므로,
 * 실행 순서와 트랜잭션 경계를 이곳에서 책임진다.
 * 각 domain 의 로직은 해당 domain service 가 갖고, 여기서는 호출 순서와 mapping 만 한다.
 */
@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductOptionService optionService;
    private final UserService userService;
    private final CouponService couponService;

    /**
     * order-01 : 주문.
     * 어느 단계에서 실패해도 이 트랜잭션이 전체를 롤백하므로,
     * 재고 · 포인트 · 쿠폰은 실패 시 그대로 유지된다.
     */
    @Transactional
    public OrderResult create(Long userId, OrderCreateRequest request) {
        UserInfo user = userService.getUser(userId);

        List<OrderItemResponse> items = distinct(request.orderItems());

        List<PurchaseResult> purchases = productService.purchase(items);                  // 재고 차감 + 금액 계산
        CouponResult couponResult = couponService.use(request.couponId(), user.getId());  // 쿠폰 사용 + 할인 금액
        int amount = orderService.calculateAmount(purchases, couponResult);               // 주문 금액 계산
        OrderInfo order = orderService.create(user.getId(), purchases, couponResult, amount);  // 주문 생성
        userService.usePoint(user.getId(), amount);                                       // 포인트 차감

        return order.toOrderResult(user);
    }

    /**
     * order-02 : 구매자 주문 리스트 조회.
     * 주문 줄이 갖고 있는 것은 id 뿐이므로 각 domain 에서 실물을 채워오고, 조립은 order VO 에게 맡긴다.
     */
    @Transactional(readOnly = true)
    public List<BuyerOrderResponse> getMyOrders(Long userId) {
        UserInfo user = userService.getUser(userId);
        List<OrderInfo> orders = orderService.getMyOrders(user.getId());

        List<ProductInfo> products = productService.getProducts(orders.stream()
                .flatMap(order -> order.getItems().stream().map(OrderItemInfo::getProductId))
                .distinct().toList());
        List<UserInfo> creators = userService.getUsers(products.stream()
                .map(ProductInfo::getCreatorId)
                .distinct().toList());
        List<OptionInfo> options = optionService.getOptions(orders.stream()
                .flatMap(order -> order.getItems().stream().map(OrderItemInfo::getOptionId))
                .distinct().toList());
        List<CouponInfo> coupons = couponService.getCoupons(orders.stream()
                .map(OrderInfo::getCouponId)
                .filter(Objects::nonNull)
                .distinct().toList());

        return OrderInfo.toBuyerOrderResponses(orders, products, creators, options, coupons);
    }

    /** order-03 : 판매자 주문 리스트 조회 (상품 생성자만, 옵션별로 묶어서 반환) */
    @Transactional(readOnly = true)
    public List<SellerOptionOrders> getProductOrders(Long userId, Long productId) {
        UserInfo user = userService.getUser(userId);
        productService.validateOwner(productId, user.getId());   // 판매자만 조회 가능 (product domain 규칙)

        List<OrderInfo> orders = orderService.getItemsByProduct(productId);
        List<UserInfo> buyers = userService.getUsers(orders.stream()
                .map(OrderInfo::getUserId)
                .distinct().toList());

        return OrderInfo.toSellerOptionOrders(orders, productId, buyers);
    }

    /**
     * order-04 : 주문 취소.
     * 재고 · 쿠폰 · 포인트를 각 domain 에게 되돌리게 한 뒤 주문을 삭제한다.
     */
    @Transactional
    public void cancel(Long userId, Long orderId) {
        UserInfo user = userService.getUser(userId);
        OrderInfo order = orderService.getOrderOwnedBy(orderId, user.getId());

        productService.restore(order.getItems());                  // 재고 복원
        couponService.restore(order.getCouponId());                // 쿠폰을 사용 가능 상태로 복원
        userService.refundPoint(user.getId(), order.getAmount());  // 포인트 복원
        orderService.delete(order.getId());
    }

    /** order-01 : 옵션 id 가 같은 주문 줄은 개수를 합산한다 (입력 순서 유지) */
    private List<OrderItemResponse> distinct(List<OrderItemResponse> items) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (OrderItemResponse item : items) {
            merged.merge(item.optionId(), item.count(), Integer::sum);
        }
        return merged.entrySet().stream()
                .map(e -> new OrderItemResponse(e.getKey(), e.getValue()))
                .toList();
    }
}
