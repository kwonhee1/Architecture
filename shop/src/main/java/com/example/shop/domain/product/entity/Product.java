package com.example.shop.domain.product.entity;

import com.example.shop.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ProductStatus.ON_SALE;
    }

    public void validateCreator(Long userId) {
        if (!this.creator.getId().equals(userId)) {
            throw new IllegalStateException("상품 등록자만 수정할 수 있습니다.");
        }
    }

    public void update(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    @Builder
    public Product(User creator, String name, int price, String description) {
        this.creator = creator;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
