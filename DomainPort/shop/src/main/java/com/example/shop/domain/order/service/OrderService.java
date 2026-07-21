package com.example.shop.domain.order.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.order.dto.*;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.repository.OrderItemRepository;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.order.service.port.OrderDomainPort;
import com.example.shop.domain.order.service.usecase.OrderUseCase;
import com.example.shop.domain.product.dto.ProductOptionResponse;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.service.port.ProductDomainPort;
import com.example.shop.domain.product.service.port.ProductOptionDomainPort;
import com.example.shop.domain.product.service.vo.ProductOptionVo.*;
import com.example.shop.domain.product.service.vo.ProductVo.*;
import com.example.shop.domain.user.dto.CouponResponse;
import com.example.shop.domain.user.dto.CreatorResponse;
import com.example.shop.domain.user.service.port.CouponDomainPort;
import com.example.shop.domain.user.service.port.UserDomainPort;
import com.example.shop.domain.user.service.vo.CouponVo.*;
import com.example.shop.domain.user.service.vo.UserVo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * order domain 의 로직 소유자.
 * <p>
 * 다른 domain 의 service 를 하나도 주입받지 않는다. user / coupon / product / option 은 모두
 * port 로만 오간다. 재고가 충분한지, 포인트가 충분한지, 쿠폰이 유효한지를 order 가 판단하지
 * 않고, 각 port 를 호출해 결과 VO 가 설명해 주는 것만 받아 흐름을 조립한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase, OrderDomainPort {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserDomainPort userPort;
    private final CouponDomainPort couponPort;
    private final ProductDomainPort productPort;
    private final ProductOptionDomainPort optionPort;

    // ────────────── UseCase (controller 전용) ──────────────

    /**
     * order-01 : 주문.
     * <p>
     * 재고 차감 · 쿠폰 사용 · 포인트 차감이 이 트랜잭션 안에서 함께 움직인다.
     * 어느 port 든 실패하면 예외가 올라와 전체가 롤백되고, 포인트 · 재고 · 쿠폰은 변하지 않는다.
     */
    @Override
    @Transactional
    public OrderResult create(Long userId, OrderCreateRequest request) {
        // 옵션 id 가 같은 주문 줄은 개수를 더한다 (입력 순서 유지)
        Map<Long, Integer> countByOptionId = new LinkedHashMap<>();
        for (OrderItemInfo item : request.orderItems()) {
            countByOptionId.merge(item.optionId(), item.count(), Integer::sum);
        }

        // 재고 차감 — 재고가 충분한지의 판단도, 단가 계산도 option 이 한다
        List<StockDecreaseResult> purchases = new ArrayList<>();
        for (Map.Entry<Long, Integer> line : countByOptionId.entrySet()) {
            purchases.add(optionPort.decreaseStock(line.getKey(), line.getValue()));
        }

        int totalAmount = purchases.stream()
                .mapToInt(StockDecreaseResult::purchaseAmount)
                .sum();

        // 쿠폰 사용 (선택) — 내 쿠폰인지 · 이미 썼는지의 판단은 coupon 이 한다
        int discount = couponPort.useCoupon(request.couponId(), userId).discountAmount();

        // 주문 금액은 쿠폰 적용 이후 금액이며 음수로 내려가지 않는다
        int finalAmount = Math.max(0, totalAmount - discount);

        // 포인트 차감 — 포인트가 충분한지의 판단은 user 가 한다
        userPort.usePoint(userId, finalAmount);

        Order order = Order.builder()
                .userId(userId)
                .couponId(request.couponId())
                .amount(finalAmount)
                .build();
        for (StockDecreaseResult purchase : purchases) {
            order.addItem(OrderItem.builder()
                    .order(order)
                    .productId(purchase.product().id())
                    .optionId(purchase.option().id())
                    .quantity(purchase.count())
                    .amount(purchase.purchaseAmount())
                    .build());
        }
        Order saved = orderRepository.save(order);

        UserInfo orderer = userPort.getUserInfo(userId);
        List<OrderItemInfo> lines = saved.getItems().stream()
                .map(i -> new OrderItemInfo(i.getOptionId(), i.getQuantity()))
                .toList();

        return new OrderResult(
                saved.getId(),
                lines,
                saved.getAmount(),
                saved.getOrderDate(),
                new CreatorResponse(orderer.name()));
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    @Override
    public List<BuyerOrderResponse> getMyOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdWithItems(userId);
        if (orders.isEmpty()) {
            return List.of();
        }
        List<OrderItem> items = orders.stream().flatMap(o -> o.getItems().stream()).toList();

        // 응답에 필요한 상품·옵션·쿠폰 정보를 각 domain 에게 VO 로 받아 와 id 로 찾게 세워 둔다
        Map<Long, ProductInfo> products = productPort.getProductInfos(
                        items.stream().map(OrderItem::getProductId).distinct().toList()).stream()
                .collect(Collectors.toMap(ProductInfo::id, Function.identity()));
        Map<Long, OptionInfo> options = optionPort.getOptionInfos(
                        items.stream().map(OrderItem::getOptionId).distinct().toList()).stream()
                .collect(Collectors.toMap(OptionInfo::id, Function.identity()));
        Map<Long, CouponInfo> coupons = couponPort.getCouponInfos(
                        orders.stream().map(Order::getCouponId).filter(Objects::nonNull).distinct().toList())
                .stream()
                .collect(Collectors.toMap(CouponInfo::id, Function.identity()));

        return orders.stream()
                .map(order -> new BuyerOrderResponse(
                        order.getId(),
                        order.getItems().stream()
                                .map(item -> new BuyerOrderLineDetail(
                                        ProductResponse.from(products.get(item.getProductId())),
                                        ProductOptionResponse.from(options.get(item.getOptionId())),
                                        item.getQuantity()))
                                .toList(),
                        order.getAmount(),
                        order.getOrderDate(),
                        CouponResponse.from(coupons.get(order.getCouponId()))))
                .toList();
    }

    /** order-03 : 판매자 주문 리스트 조회 (상품 생성자만, 옵션별로 묶어서 반환) */
    @Override
    public List<SellerOptionOrders> getProductOrders(Long productId, Long userId) {
        // 판매자가 맞는지는 product 가 판단한다
        if (!productPort.checkOwner(productId, userId)) {
            throw new BusinessException(ErrorCode.NOT_PRODUCT_OWNER);
        }

        List<OrderItem> items = orderItemRepository.findByProductIdWithOrder(productId);
        if (items.isEmpty()) {
            return List.of();
        }

        Map<Long, UserInfo> buyers = userPort.getUserInfos(items.stream()
                        .map(i -> i.getOrder().getUserId())
                        .distinct()
                        .toList()).stream()
                .collect(Collectors.toMap(UserInfo::id, Function.identity()));

        Map<Long, List<SellerOrderEntry>> grouped = new LinkedHashMap<>();
        for (OrderItem item : items) {
            grouped.computeIfAbsent(item.getOptionId(), k -> new ArrayList<>())
                    .add(new SellerOrderEntry(
                            BuyerResponse.from(buyers.get(item.getOrder().getUserId())),
                            item.getQuantity()));
        }
        return grouped.entrySet().stream()
                .map(e -> new SellerOptionOrders(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * order-04 : 주문 취소.
     * 재고 · 쿠폰 · 포인트를 각 domain 에게 되돌리게 시키고 order 를 삭제한다.
     * 내가 주문한 주문만 취소 가능하다.
     */
    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.NOT_ORDER_OWNER);
        }

        for (OrderItem item : order.getItems()) {
            optionPort.restoreStock(item.getOptionId(), item.getQuantity());
        }
        couponPort.restoreCoupon(order.getCouponId());
        userPort.refundPoint(order.getUserId(), order.getAmount());

        orderRepository.delete(order);
    }

    // ────────────── DomainPort (타 domain 전용) ──────────────

    @Override
    public boolean existsByProduct(Long productId) {
        return orderItemRepository.countByProductId(productId) > 0;
    }

    @Override
    public boolean existsByOption(Long optionId) {
        return orderItemRepository.countByOptionId(optionId) > 0;
    }

}
