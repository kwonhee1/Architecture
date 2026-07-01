package com.example.shop.domain.product.controller;

import com.example.shop.domain.product.dto.ProductCreateRequest;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(loginUserId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getOne(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getOne(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long productId,
                                                  @Valid @RequestBody ProductCreateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(productService.update(productId, loginUserId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId,
                                       @RequestAttribute Long loginUserId) {
        productService.delete(productId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
