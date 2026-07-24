package com.example.shop.order.domain.model;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.model.vo.Money;
import com.example.shop.order.domain.model.vo.OrderId;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * order aggregate root.
 *
 * <p>주문 금액 계산 규칙("단가 합 - 쿠폰 할인, 단 0 밑으로 내려가지 않는다")과 소유자 확인을
 * 스스로 소유한다. 회원·쿠폰·옵션은 각각 다른 aggregate 이므로 식별자로만 참조하고, 포인트 차감·
 * 재고 차감·쿠폰 사용 같은 <b>다른 aggregate 의 상태 변경</b>은 이 안에서 하지 않는다.
 * 그 조율은 application(PlaceOrderService)이 한 트랜잭션으로 담당한다.</p>
 */
public class Order {

    private final OrderId id;        // 신규 생성 시 null
    private final BuyerId buyerId;
    private final Long couponId;     // 쿠폰 미사용 시 null
    private final List<OrderLine> lines;
    private final Money amount;
    private final LocalDate orderDate;

    private Order(OrderId id, BuyerId buyerId, Long couponId,
                  List<OrderLine> lines, Money amount, LocalDate orderDate) {
        this.id = id;
        this.buyerId = buyerId;
        this.couponId = couponId;
        this.lines = lines;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    /**
     * order-01 : 주문 생성.
     * 옵션은 필수이며, 금액은 (단가 합 - 할인) 이되 음수로 내려가지 않는다. 주문 날짜는 date 까지만.
     */
    public static Order place(BuyerId buyerId, Long couponId, List<OrderLine> lines, long discount) {
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_ITEM_REQUIRED);
        }
        Money total = lines.stream().map(OrderLine::subtotal).reduce(Money.zero(), Money::plus);
        Money finalAmount = total.minusToZero(discount);
        return new Order(null, buyerId, couponId, List.copyOf(lines), finalAmount, LocalDate.now());
    }

    public static Order reconstitute(OrderId id, BuyerId buyerId, Long couponId,
                                     List<OrderLine> lines, long amount, LocalDate orderDate) {
        return new Order(id, buyerId, couponId, List.copyOf(lines), Money.of(amount), orderDate);
    }

    /** order-04 : 내가 주문한 주문만 취소할 수 있다. */
    public void validateOwner(BuyerId requester) {
        if (!this.buyerId.equals(requester)) {
            throw new BusinessException(ErrorCode.NOT_ORDER_OWNER);
        }
    }

    public boolean hasCoupon() {
        return couponId != null;
    }

    public OrderId id() {
        return id;
    }

    public BuyerId buyerId() {
        return buyerId;
    }

    public Long couponId() {
        return couponId;
    }

    /** 불변 entity 의 읽기 전용 목록 (재고 복원·응답 구성에 사용). */
    public List<OrderLine> lines() {
        return Collections.unmodifiableList(lines);
    }

    public long amount() {
        return amount.value();
    }

    public LocalDate orderDate() {
        return orderDate;
    }
}
