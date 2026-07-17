package com.example.shop.domain.product.controller;

import com.example.shop.common.auth.AuthFilter;
import com.example.shop.domain.product.dto.request.ProductCreateRequest;
import com.example.shop.domain.product.dto.request.ProductUpdateRequest;
import com.example.shop.domain.product.dto.response.ProductDetailResponse;
import com.example.shop.domain.product.dto.response.ProductResponse;
import com.example.shop.domain.product.facade.ProductFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    /** 인증 없이 접근 가능한 공개 경로 등록 (상품 목록 / 상품 상세). /products/me 등은 제외 */
    static {
        AuthFilter.registerPublic("GET", "/products");
        AuthFilter.registerPublic("GET", "/products/\\d+");
    }

    private final ProductFacade productFacade;

    /** product-01 : 새 상품 등록 */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productFacade.create(loginUserId, request));
    }

    /** product-03 : 상품 리스트 조회 */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productFacade.getAllProducts());
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    @GetMapping("/me")
    public ResponseEntity<List<ProductResponse>> getMyProducts(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(productFacade.getMyProducts(loginUserId));
    }

    /** product-04 : 상품 상세 조회 */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productFacade.getProductDetail(productId));
    }

    /** product-02 : 상품 정보 수정 */
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long productId,
                                                  @RequestBody ProductUpdateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(productFacade.update(loginUserId, productId, request));
    }
}
