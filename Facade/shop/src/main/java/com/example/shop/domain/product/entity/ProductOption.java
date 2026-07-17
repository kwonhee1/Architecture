package com.example.shop.domain.product.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * option 은 product 와 같은 domain 이므로 Product 를 직접 FK 로 참조한다.
 */
@Entity
@Table(name = "product_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String description;

    /** 옵션 추가 금액 (추가 금액 + 상품 금액 > 0) */
    @Column(nullable = false)
    private int additionalPrice;

    /** 재고 개수 (>= 0) */
    @Column(nullable = false)
    private int stock;

    @Builder
    public ProductOption(Product product, String description, int additionalPrice, int stock) {
        this.product = product;
        this.description = description;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    /** 추가 금액 변경 (추가 금액 + 상품 금액 > 0) */
    public void changeAdditionalPrice(int additionalPrice) {
        if (this.product.getPrice() + additionalPrice <= 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_PRICE);
        }
        this.additionalPrice = additionalPrice;
    }

    public void changeStock(int stock) {
        if (stock < 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_STOCK);
        }
        this.stock = stock;
    }

    /** order-01 : 주문 개수만큼 재고 차감 (부족하면 실패) */
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

    /** order-04 : 주문 취소 시 재고 복원 */
    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    /** 이 옵션으로 quantity 개를 살 때의 금액 (상품 금액 + 추가 금액) */
    public int purchasePrice(int quantity) {
        return (this.product.getPrice() + this.additionalPrice) * quantity;
    }
}
