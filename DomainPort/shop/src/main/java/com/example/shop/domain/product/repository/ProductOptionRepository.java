package com.example.shop.domain.product.repository;

import com.example.shop.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    @Query("select o from ProductOption o join fetch o.product p where p.id = :productId")
    List<ProductOption> findByProductIdWithProduct(@Param("productId") Long productId);

    @Query("select o from ProductOption o join fetch o.product where o.id = :optionId")
    Optional<ProductOption> findByIdWithProduct(@Param("optionId") Long optionId);

    @Query("select o from ProductOption o join fetch o.product where o.id in :optionIds")
    List<ProductOption> findByIdInWithProduct(@Param("optionIds") List<Long> optionIds);
}
