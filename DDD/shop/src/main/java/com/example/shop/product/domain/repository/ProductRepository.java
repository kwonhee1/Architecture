package com.example.shop.product.domain.repository;

import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(ProductId id);

    /** product-03 : 전체 상품. */
    List<Product> findAll();

    /** product-05 : 특정 생성자의 상품. */
    List<Product> findByCreatorId(CreatorId creatorId);
}
