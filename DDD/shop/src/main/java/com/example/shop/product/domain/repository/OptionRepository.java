package com.example.shop.product.domain.repository;

import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;

import java.util.List;
import java.util.Optional;

public interface OptionRepository {

    Option save(Option option);

    Optional<Option> findById(OptionId id);

    /** product-04 : 상품 상세의 옵션 리스트. */
    List<Option> findByProductId(ProductId productId);

    void delete(Option option);
}
