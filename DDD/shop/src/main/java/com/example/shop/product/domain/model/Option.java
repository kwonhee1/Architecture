package com.example.shop.product.domain.model;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.product.domain.model.vo.Money;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.model.vo.Stock;

/**
 * option aggregate root (상품 옵션).
 *
 * <p>Product 와는 별개의 aggregate 이며, 소속 상품은 식별자(ProductId)로만 참조한다.
 * "추가 금액 + 상품 금액 > 0", "재고 >= 0", "재고보다 많이 뺄 수 없다" 규칙을 소유한다.
 * 추가 금액은 음수일 수 있으므로(상품 금액과의 합만 > 0 이면 됨) VO 대신 원시값으로 다룬다.
 * 상품 금액이 필요한 검증은, 값을 인자로 받아 판정은 이 aggregate 안에서 한다.</p>
 */
public class Option {

    /** product-01 : 상품 등록 시 함께 만들어지는 기본 옵션 스펙 */
    public static final String DEFAULT_OPTION_DESCRIPTION = "기본 옵션";

    private final OptionId id;       // 신규 생성 시 null
    private final ProductId productId;
    private String description;
    private long additionalPrice;
    private Stock stock;

    private Option(OptionId id, ProductId productId, String description, long additionalPrice, Stock stock) {
        this.id = id;
        this.productId = productId;
        this.description = description;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }

    /** option-01 : 옵션 생성 (추가 금액 + 상품 금액 > 0). */
    public static Option create(ProductId productId, String description,
                                long additionalPrice, long stock, Money productPrice) {
        validateCombinedPrice(productPrice, additionalPrice);
        return new Option(null, productId, description, additionalPrice, Stock.of(stock));
    }

    /** product-01 : 기본 옵션 { "기본 옵션", 0, 재고 0 }. */
    public static Option createDefault(ProductId productId) {
        return new Option(null, productId, DEFAULT_OPTION_DESCRIPTION, 0L, Stock.of(0L));
    }

    public static Option reconstitute(OptionId id, ProductId productId, String description,
                                      long additionalPrice, long stock) {
        return new Option(id, productId, description, additionalPrice, Stock.of(stock));
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    /** option-02 : 추가 금액 변경 (추가 금액 + 상품 금액 > 0). */
    public void changeAdditionalPrice(long additionalPrice, Money productPrice) {
        validateCombinedPrice(productPrice, additionalPrice);
        this.additionalPrice = additionalPrice;
    }

    /** option-02 : 재고 변경 (재고 >= 0 은 Stock 이 보장). */
    public void changeStock(long stock) {
        this.stock = Stock.of(stock);
    }

    /** 주문 시 재고 차감. */
    public void decreaseStock(long quantity) {
        this.stock = this.stock.decrease(quantity);
    }

    /** 주문 취소 시 재고 복원. */
    public void increaseStock(long quantity) {
        this.stock = this.stock.increase(quantity);
    }

    /** 이 옵션의 단가 = 상품 금액 + 추가 금액. */
    public long unitPrice(Money productPrice) {
        return productPrice.plus(additionalPrice);
    }

    private static void validateCombinedPrice(Money productPrice, long additionalPrice) {
        if (productPrice.plus(additionalPrice) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_PRICE);
        }
    }

    public OptionId id() {
        return id;
    }

    public ProductId productId() {
        return productId;
    }

    public String description() {
        return description;
    }

    public long additionalPrice() {
        return additionalPrice;
    }

    public long stock() {
        return stock.value();
    }
}
