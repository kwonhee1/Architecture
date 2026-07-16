package com.example.shop.domain.option.controller;

import com.example.shop.domain.option.dto.ProductOptionCreateRequest;
import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.option.dto.ProductOptionUpdateRequest;
import com.example.shop.domain.option.service.ProductOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService optionService;

    /** option-01 : 옵션 생성 */
    @PostMapping("/options/{productId}")
    public ResponseEntity<ProductOptionResponse> create(@PathVariable Long productId,
                                                        @Valid @RequestBody ProductOptionCreateRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(optionService.create(productId, loginUserId, request));
    }

    /** option-02 : 옵션 수정 */
    @PatchMapping("/options/{optionId}")
    public ResponseEntity<ProductOptionResponse> update(@PathVariable Long optionId,
                                                        @RequestBody ProductOptionUpdateRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(optionService.update(optionId, loginUserId, request));
    }

    /** option-02 : 옵션 삭제 */
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> delete(@PathVariable Long optionId,
                                       @RequestAttribute Long loginUserId) {
        optionService.delete(optionId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
