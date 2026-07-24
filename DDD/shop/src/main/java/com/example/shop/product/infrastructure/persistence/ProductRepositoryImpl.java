package com.example.shop.product.infrastructure.persistence;

import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return ProductMapper.toDomain(jpaRepository.save(ProductMapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value()).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(ProductMapper::toDomain).toList();
    }

    @Override
    public List<Product> findByCreatorId(CreatorId creatorId) {
        return jpaRepository.findByCreatorId(creatorId.value()).stream()
                .map(ProductMapper::toDomain)
                .toList();
    }
}
