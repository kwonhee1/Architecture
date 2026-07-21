package com.example.shop.domain.order.service.usecase;

import com.example.shop.domain.order.dto.BuyerOrderResponse;
import com.example.shop.domain.order.dto.OrderCreateRequest;
import com.example.shop.domain.order.dto.OrderResult;
import com.example.shop.domain.order.dto.SellerOptionOrders;

import java.util.List;

/** controller ↔ order service 계약 */
public interface OrderUseCase {

    /** order-01 : 주문 */
    OrderResult create(Long userId, OrderCreateRequest request);

    /** order-02 : 구매자 주문 리스트 조회 */
    List<BuyerOrderResponse> getMyOrders(Long userId);

    /** order-03 : 판매자 주문 리스트 조회 */
    List<SellerOptionOrders> getProductOrders(Long productId, Long userId);

    /** order-04 : 주문 취소 */
    void cancel(Long orderId, Long userId);
}
