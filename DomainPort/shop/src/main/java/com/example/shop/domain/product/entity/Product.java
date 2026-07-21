package com.example.shop.domain.product.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * user 는 다른 domain 이므로 User 를 참조하지 않고 creatorId(Long) 로만 연결한다.
 * 생성자의 이름이 필요하면 product service 가 UserDomainPort 로 물어본다.
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

    public boolean isCreator(Long userId) {
        return this.creatorId.equals(userId);
    }

    /** 상품 생성자만 수정 가능 */
    public void validateOwner(Long userId) {
        if (!isCreator(userId)) {
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
