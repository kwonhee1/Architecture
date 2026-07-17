package com.example.shop.domain.order.repository;

import com.example.shop.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** product-02 : 특정 상품에 대한 주문이 존재하는지 */
    boolean existsByProductId(Long productId);

    /** option-02 : 특정 옵션에 대한 주문이 존재하는지 */
    boolean existsByOptionId(Long optionId);

    /** order-03 : 특정 상품의 모든 제품 주문 (구매자 확인을 위해 order 를 함께 조회) */
    @Query("select i from OrderItem i left join fetch i.order where i.productId = :productId")
    List<OrderItem> findByProductIdWithOrder(@Param("productId") Long productId);
}
