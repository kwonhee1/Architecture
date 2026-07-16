package com.example.shop.domain.option.repository;

import com.example.shop.domain.option.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    @Query("select o from ProductOption o left join fetch o.product p where p.id = :productId")
    List<ProductOption> findByProductIdWithProduct(@Param("productId") Long productId);
}
