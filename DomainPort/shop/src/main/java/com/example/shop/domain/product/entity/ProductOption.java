package com.example.shop.domain.product.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * option 은 product 와 같은 domain 에 묶여 있으므로 Product 를 FK 로 직접 참조한다.
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
    @JoinColumn(name = "product_id", nullable = false)
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

    /** 옵션 1개의 판매 단가 = 상품 금액 + 옵션 추가 금액 */
    public int unitPrice() {
        return this.product.getPrice() + this.additionalPrice;
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

    /** 재고가 충분한지는 option 이 판단한다 */
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}
