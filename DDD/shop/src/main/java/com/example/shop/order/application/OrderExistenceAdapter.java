package com.example.shop.order.application;

import com.example.shop.order.domain.repository.OrderRepository;
import com.example.shop.product.application.port.OrderExistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * product context 가 요구한 {@link OrderExistencePort} 를 order context 가 구현한다.
 * order 의 domain 을 밖으로 노출하지 않고, 원시값 in / boolean out 계약만 만족시킨다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderExistenceAdapter implements OrderExistencePort {

    private final OrderRepository orderRepository;

    @Override
    public boolean existsForProduct(long productId) {
        return orderRepository.existsByProductId(productId);
    }

    @Override
    public boolean existsForOption(long optionId) {
        return orderRepository.existsByOptionId(optionId);
    }
}
