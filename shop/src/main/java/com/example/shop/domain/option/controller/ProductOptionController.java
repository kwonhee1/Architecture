package com.example.shop.domain.option.controller;

import com.example.shop.domain.option.dto.ProductOptionRequest;
import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.option.service.ProductOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/options")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService optionService;

    @PostMapping
    public ResponseEntity<ProductOptionResponse> create(@PathVariable Long productId,
                                                        @Valid @RequestBody ProductOptionRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.create(productId, loginUserId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductOptionResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(optionService.getByProduct(productId));
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<ProductOptionResponse> update(@PathVariable Long productId,
                                                        @PathVariable Long optionId,
                                                        @Valid @RequestBody ProductOptionRequest request) {
        return ResponseEntity.ok(optionService.update(optionId, request));
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId,
                                       @PathVariable Long optionId) {
        optionService.delete(optionId);
        return ResponseEntity.noContent().build();
    }
}
