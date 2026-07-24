package com.example.shop.product.presentation;

import com.example.shop.common.auth.AuthFilter;
import com.example.shop.product.application.ProductApplicationService;
import com.example.shop.product.presentation.dto.ProductCreateRequest;
import com.example.shop.product.presentation.dto.ProductDetailResponse;
import com.example.shop.product.presentation.dto.ProductResponse;
import com.example.shop.product.presentation.dto.ProductUpdateRequest;
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

    /** 인증 없이 접근 가능한 공개 경로 (상품 목록 / 상품 상세). /products/me 는 제외. */
    static {
        AuthFilter.registerPublic("GET", "/products");
        AuthFilter.registerPublic("GET", "/products/\\d+");
    }

    private final ProductApplicationService productService;

    /** product-01 : 새 상품 등록 */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.from(productService.create(loginUserId, request.toCommand())));
    }

    /** product-03 : 상품 리스트 조회 */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts().stream()
                .map(ProductResponse::from).toList());
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    @GetMapping("/me")
    public ResponseEntity<List<ProductResponse>> getMyProducts(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(productService.getMyProducts(loginUserId).stream()
                .map(ProductResponse::from).toList());
    }

    /** product-04 : 상품 상세 조회 */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(ProductDetailResponse.from(productService.getProductDetail(productId)));
    }

    /** product-02 : 상품 정보 수정 */
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long productId,
                                                  @RequestBody ProductUpdateRequest request,
                                                  @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(
                ProductResponse.from(productService.update(productId, loginUserId, request.toCommand())));
    }
}
