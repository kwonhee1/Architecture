package com.example.shop.domain.order.controller;

import com.example.shop.domain.order.dto.request.OrderCreateRequest;
import com.example.shop.domain.order.dto.response.BuyerOrderResponse;
import com.example.shop.domain.order.dto.response.OrderResult;
import com.example.shop.domain.order.dto.response.SellerOptionOrders;
import com.example.shop.domain.order.facade.OrderFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    /** order-01 : 주문 */
    @PostMapping
    public ResponseEntity<OrderResult> create(@Valid @RequestBody OrderCreateRequest request,
                                              @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderFacade.create(loginUserId, request));
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    @GetMapping("/buyer")
    public ResponseEntity<List<BuyerOrderResponse>> getMyOrders(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderFacade.getMyOrders(loginUserId));
    }

    /** order-03 : 판매자 주문 리스트 조회 */
    @GetMapping("/{productId}/seller")
    public ResponseEntity<List<SellerOptionOrders>> getProductOrders(@PathVariable Long productId,
                                                                     @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderFacade.getProductOrders(loginUserId, productId));
    }

    /** order-04 : 주문 취소 */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId,
                                       @RequestAttribute Long loginUserId) {
        orderFacade.cancel(loginUserId, orderId);
        return ResponseEntity.noContent().build();
    }
}
