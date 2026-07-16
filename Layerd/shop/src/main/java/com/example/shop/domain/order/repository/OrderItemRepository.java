package com.example.shop.domain.order.repository;

import com.example.shop.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /** 특정 상품에 대한 주문(제품 주문)이 존재하는지 */
    boolean existsByProductId(Long productId);

    /** 특정 옵션에 대한 주문(제품 주문)이 존재하는지 */
    boolean existsByOptionId(Long optionId);

    /** 판매자 주문 조회용 - 특정 상품의 모든 제품 주문 */
    List<OrderItem> findByProductId(Long productId);
}
