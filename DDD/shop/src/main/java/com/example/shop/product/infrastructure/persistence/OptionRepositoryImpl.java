package com.example.shop.product.infrastructure.persistence;

import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {

    private final OptionJpaRepository jpaRepository;

    @Override
    public Option save(Option option) {
        return OptionMapper.toDomain(jpaRepository.save(OptionMapper.toEntity(option)));
    }

    @Override
    public Optional<Option> findById(OptionId id) {
        return jpaRepository.findById(id.value()).map(OptionMapper::toDomain);
    }

    @Override
    public List<Option> findByProductId(ProductId productId) {
        return jpaRepository.findByProductId(productId.value()).stream()
                .map(OptionMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Option option) {
        jpaRepository.deleteById(option.id().value());
    }
}
