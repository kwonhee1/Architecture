package com.example.shop.domain.option.entity;

import com.example.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

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
    private String name;

    @Column(nullable = false)
    private int additionalPrice;

    @Column(nullable = false)
    private int stock;

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    public void update(String name, int additionalPrice, int stock) {
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }

    @Builder
    public ProductOption(Product product, String name, int additionalPrice, int stock) {
        this.product = product;
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }
}
