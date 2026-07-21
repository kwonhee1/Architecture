package com.example.shop.domain.order.repository;

import com.example.shop.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** 특정 상품에 대한 제품 주문 수 (product-02 금액 수정 제약) */
    long countByProductId(Long productId);

    /** 특정 옵션에 대한 제품 주문 수 (option-02 수정·삭제 제약) */
    long countByOptionId(Long optionId);

    /** order-03 판매자 주문 조회용 - 특정 상품의 모든 제품 주문 */
    @Query("select i from OrderItem i join fetch i.order where i.productId = :productId")
    List<OrderItem> findByProductIdWithOrder(@Param("productId") Long productId);
}
