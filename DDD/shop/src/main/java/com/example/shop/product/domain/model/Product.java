package com.example.shop.product.domain.model;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.Money;
import com.example.shop.product.domain.model.vo.ProductId;

/**
 * product aggregate root.
 *
 * <p>"판매 금액 > 0", "생성자만 수정 가능" 같은 규칙을 스스로 소유한다.
 * 옵션(Option)은 별도 aggregate 이며, 여기서는 참조하지 않는다 (aggregate 는 작게 유지).
 * "order 존재 시 금액 수정 불가" 는 다른 aggregate(order) 를 봐야 하므로 aggregate 밖(application)
 * 에서 판정한 결과를 받아 처리한다.</p>
 */
public class Product {

    private final ProductId id;      // 신규 생성 시 null
    private final CreatorId creatorId;
    private String description;
    private Money price;

    private Product(ProductId id, CreatorId creatorId, String description, Money price) {
        if (!price.isPositive()) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_PRICE);
        }
        this.id = id;
        this.creatorId = creatorId;
        this.description = description;
        this.price = price;
    }

    /** product-01 : 새 상품 생성. */
    public static Product register(CreatorId creatorId, String description, Money price) {
        return new Product(null, creatorId, description, price);
    }

    public static Product reconstitute(ProductId id, CreatorId creatorId, String description, Money price) {
        return new Product(id, creatorId, description, price);
    }

    /** product-02 : 상품 생성자만 수정 가능. */
    public void validateOwner(CreatorId requester) {
        if (!this.creatorId.equals(requester)) {
            throw new BusinessException(ErrorCode.NOT_PRODUCT_OWNER);
        }
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    /** product-02 : 금액 수정 (판매 금액 > 0). order 존재 여부는 application 이 사전 판정한다. */
    public void changePrice(Money price) {
        if (!price.isPositive()) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_PRICE);
        }
        this.price = price;
    }

    public ProductId id() {
        return id;
    }

    public CreatorId creatorId() {
        return creatorId;
    }

    public String description() {
        return description;
    }

    public Money price() {
        return price;
    }
}
