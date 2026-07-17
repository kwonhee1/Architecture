package com.example.shop.domain.product.repository;

import com.example.shop.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    List<ProductOption> findByIdIn(Collection<Long> optionIds);

    /** 금액 계산·검증에 상품 금액이 필요하므로 product 를 함께 조회한다 */
    @Query("select o from ProductOption o join fetch o.product where o.id = :optionId")
    Optional<ProductOption> findByIdWithProduct(@Param("optionId") Long optionId);
}
