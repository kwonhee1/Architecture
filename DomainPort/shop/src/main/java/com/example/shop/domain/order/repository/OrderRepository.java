package com.example.shop.domain.order.repository;

import com.example.shop.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o left join fetch o.items where o.userId = :userId")
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);
}
