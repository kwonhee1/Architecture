package com.example.shop.domain.product.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, updatable = false)
    private User creator;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 판매 금액 > 0 */
    @Column(nullable = false)
    private int price;

    @Builder
    public Product(User creator, String description, int price) {
        this.creator = creator;
        this.description = description;
        this.price = price;
    }

    public boolean isCreator(Long userId) {
        return this.creator.getId().equals(userId);
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
