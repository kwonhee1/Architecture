package com.example.shop.domain.order.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderInfo;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.repository.OrderItemRepository;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.product.dto.PurchaseResult;
import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.service.vo.CouponResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * order domain service. order domain 의 로직만 담당한다.
 * product / user domain 의 service 는 호출하지 않고,
 * 이미 처리가 끝난 결과(PurchaseResult / Discount)만 받아서 주문을 만든다.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * order-01 : 주문 금액 계산.
     * 재고 차감(product)과 쿠폰 사용(coupon)이 끝난 결과를 받아 결제할 금액을 정한다.
     * 쿠폰 적용 이후 금액은 음수로 내려가지 않는다.
     */
    public int calculateAmount(List<PurchaseResult> purchases, CouponResult couponResult) {
        int total = purchases.stream().mapToInt(PurchaseResult::purchasePrice).sum();
        return Math.max(0, total - couponResult.getAmount());
    }

    /**
     * order-01 : 주문 생성.
     * 금액 계산은 calculateAmount 가 끝냈고, 여기서는 확정된 금액으로 주문을 기록하기만 한다.
     */
    public OrderInfo create(Long userId, List<PurchaseResult> purchases, CouponResult couponResult,
                            int amount) {
        CouponInfo usedCoupon = couponResult.getCouponInfo();
        Order order = Order.builder()
                .userId(userId)
                .couponId(usedCoupon != null ? usedCoupon.id() : null)
                .amount(amount)
                .build();

        for (PurchaseResult purchase : purchases) {
            order.addItem(OrderItem.builder()
                    .order(order)
                    .productId(purchase.productId())
                    .optionId(purchase.optionId())
                    .quantity(purchase.count())
                    .lineAmount(purchase.purchasePrice())
                    .build());
        }
        return OrderInfo.from(orderRepository.save(order));
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    public List<OrderInfo> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderInfo::from)
                .toList();
    }

    /**
     * order-03 : 특정 상품의 주문 줄 조회.
     * 조회는 주문 줄 기준이고, 구매자는 주문에 있으므로 주문 단위(OrderInfo)로 접어서 돌려준다.
     * (한 주문에서 같은 상품이 여러 줄 나올 수 있어 중복을 제거한다)
     */
    public List<OrderInfo> getItemsByProduct(Long productId) {
        return orderItemRepository.findByProductIdWithOrder(productId).stream()
                .map(OrderItem::getOrder)
                .map(OrderInfo::from)
                .toList();
    }

    /** order-04 : 내가 주문한 주문만 취소할 수 있다 */
    public OrderInfo getOrderOwnedBy(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.NOT_ORDER_OWNER);
        }
        return OrderInfo.from(order);
    }

    /** order-04 : 주문 삭제 */
    public void delete(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    /** product-02 : 상품에 주문이 있는지 (product domain 이 금액 수정 가능 여부를 판단하는 데 쓴다) */
    public boolean existsOrderForProduct(Long productId) {
        return orderItemRepository.existsByProductId(productId);
    }

    /** option-02 : 옵션에 주문이 있는지 (option domain 이 수정/삭제 가능 여부를 판단하는 데 쓴다) */
    public boolean existsOrderForOption(Long optionId) {
        return orderItemRepository.existsByOptionId(optionId);
    }
}
