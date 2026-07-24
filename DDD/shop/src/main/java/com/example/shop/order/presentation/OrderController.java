package com.example.shop.order.presentation;

import com.example.shop.order.application.CancelOrderService;
import com.example.shop.order.application.OrderQueryService;
import com.example.shop.order.application.PlaceOrderService;
import com.example.shop.order.presentation.dto.BuyerOrderResponse;
import com.example.shop.order.presentation.dto.OrderCreateRequest;
import com.example.shop.order.presentation.dto.OrderResponse;
import com.example.shop.order.presentation.dto.SellerOrderResponse;
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

    private final PlaceOrderService placeOrderService;
    private final CancelOrderService cancelOrderService;
    private final OrderQueryService orderQueryService;

    /** order-01 : 주문 */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request,
                                                @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderResponse.from(placeOrderService.place(loginUserId, request.toCommand())));
    }

    /** order-02 : 구매자 주문 리스트 조회 */
    @GetMapping("/buyer")
    public ResponseEntity<List<BuyerOrderResponse>> getMyOrders(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderQueryService.getMyOrders(loginUserId).stream()
                .map(BuyerOrderResponse::from).toList());
    }

    /** order-03 : 판매자 주문 리스트 조회 */
    @GetMapping("/{productId}/seller")
    public ResponseEntity<List<SellerOrderResponse>> getProductOrders(@PathVariable Long productId,
                                                                      @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderQueryService.getProductOrders(productId, loginUserId).stream()
                .map(SellerOrderResponse::from).toList());
    }

    /** order-04 : 주문 취소 */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId,
                                       @RequestAttribute Long loginUserId) {
        cancelOrderService.cancel(loginUserId, orderId);
        return ResponseEntity.noContent().build();
    }
}
