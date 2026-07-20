package com.example.shop.domain.product.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * creator 는 다른 domain(user)의 entity 이므로 Long id 로만 참조한다.
 * (같은 domain 인 ProductOption 만 직접 FK 로 연결된다)
 */
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false, updatable = false)
    private Long creatorId;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 판매 금액 > 0 */
    @Column(nullable = false)
    private int price;

    @Builder
    public Product(Long creatorId, String description, int price) {
        this.creatorId = creatorId;
        this.description = description;
        this.price = price;
    }

    /** 상품 생성자만 수정 가능 */
    public void validateOwner(Long userId) {
        if (!this.creatorId.equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PRODUCT_OWNER);
        }
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changePrice(int price) {
        if (price <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_PRICE);
        }
        this.price = price;
    }
}
