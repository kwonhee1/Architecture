package com.example.shop.domain.order.controller;

import com.example.shop.domain.order.dto.BuyerOrderResponse;
import com.example.shop.domain.order.dto.OrderCreateRequest;
import com.example.shop.domain.order.dto.OrderResult;
import com.example.shop.domain.order.dto.SellerOptionOrders;
import com.example.shop.domain.order.service.usecase.OrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    /** order-01 : 주문 */
    @PostMapping("/orders")
    public ResponseEntity<OrderResult> create(@Valid @RequestBody OrderCreateRequest request,
                                              @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderUseCase.create(loginUserId, request));
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    @GetMapping("/orders/buyer")
    public ResponseEntity<List<BuyerOrderResponse>> getMyOrders(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderUseCase.getMyOrders(loginUserId));
    }

    /** order-03 : 판매자 주문 리스트 조회 */
    @GetMapping("/orders/{productId}/seller")
    public ResponseEntity<List<SellerOptionOrders>> getProductOrders(@PathVariable Long productId,
                                                                     @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderUseCase.getProductOrders(productId, loginUserId));
    }

    /** order-04 : 주문 취소 */
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId,
                                       @RequestAttribute Long loginUserId) {
        orderUseCase.cancel(orderId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
