package com.example.shop.order.infrastructure.persistence;

import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.model.vo.OrderId;
import com.example.shop.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return OrderMapper.toDomain(jpaRepository.save(OrderMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findByBuyerId(BuyerId buyerId) {
        return jpaRepository.findByBuyerId(buyerId.value()).stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByProductId(long productId) {
        return jpaRepository.findDistinctByLines_ProductId(productId).stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProductId(long productId) {
        return jpaRepository.existsByLines_ProductId(productId);
    }

    @Override
    public boolean existsByOptionId(long optionId) {
        return jpaRepository.existsByLines_OptionId(optionId);
    }

    @Override
    public void delete(Order order) {
        jpaRepository.deleteById(order.id().value());
    }
}
