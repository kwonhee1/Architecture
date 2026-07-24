package com.example.shop.order.domain.model;

import com.example.shop.order.domain.model.vo.Money;

/**
 * Order aggregate 내부 entity (제품 주문 한 줄).
 *
 * <p>Order 를 통해서만 만들어지고 다뤄진다. 소속 상품·옵션은 식별자로만 참조하며,
 * 주문 시점의 단가(unitPrice)를 스냅샷으로 보관한다. 불변이라 밖으로 나가도 안전하다.</p>
 */
public class OrderLine {

    private final Long id;       // 신규 생성 시 null
    private final long productId;
    private final long optionId;
    private final int quantity;
    private final Money unitPrice;

    private OrderLine(Long id, long productId, long optionId, int quantity, Money unitPrice) {
        this.id = id;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderLine of(long productId, long optionId, int quantity, long unitPrice) {
        return new OrderLine(null, productId, optionId, quantity, Money.of(unitPrice));
    }

    public static OrderLine reconstitute(Long id, long productId, long optionId, int quantity, long unitPrice) {
        return new OrderLine(id, productId, optionId, quantity, Money.of(unitPrice));
    }

    Money subtotal() {
        return unitPrice.times(quantity);
    }

    public Long id() {
        return id;
    }

    public long productId() {
        return productId;
    }

    public long optionId() {
        return optionId;
    }

    public int quantity() {
        return quantity;
    }

    public long unitPrice() {
        return unitPrice.value();
    }
}
