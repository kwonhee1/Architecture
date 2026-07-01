package com.example.shop.domain.order.controller;

import com.example.shop.domain.order.dto.OrderCreateRequest;
import com.example.shop.domain.order.dto.OrderResponse;
import com.example.shop.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request,
                                                @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(orderService.create(loginUserId, request)));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderService.getMyOrders(loginUserId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOne(@PathVariable Long orderId,
                                                @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderService.getOne(orderId, loginUserId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long orderId,
                                                @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(orderService.cancel(orderId, loginUserId));
    }
}
