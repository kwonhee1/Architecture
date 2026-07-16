package com.example.shop.domain.order.entity;

import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProductOption option;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int unitPrice;

    public int getSubtotal() {
        return this.unitPrice * this.quantity;
    }

    @Builder
    public OrderItem(Order order, Product product, ProductOption option, int quantity, int unitPrice) {
        this.order = order;
        this.product = product;
        this.option = option;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
