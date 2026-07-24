package com.example.shop.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByBuyerId(Long buyerId);

    List<OrderJpaEntity> findDistinctByLines_ProductId(Long productId);

    boolean existsByLines_ProductId(Long productId);

    boolean existsByLines_OptionId(Long optionId);
}
