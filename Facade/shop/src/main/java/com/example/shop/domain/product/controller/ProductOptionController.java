package com.example.shop.domain.product.controller;

import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.dto.request.ProductOptionCreateRequest;
import com.example.shop.domain.product.dto.request.ProductOptionUpdateRequest;
import com.example.shop.domain.product.facade.ProductOptionFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionFacade optionFacade;

    /** option-01 : 옵션 생성 */
    @PostMapping("/{productId}")
    public ResponseEntity<OptionInfo> create(@PathVariable Long productId,
                                                        @Valid @RequestBody ProductOptionCreateRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(optionFacade.create(loginUserId, productId, request));
    }

    /** option-02 : 옵션 수정 */
    @PatchMapping("/{optionId}")
    public ResponseEntity<OptionInfo> update(@PathVariable Long optionId,
                                                        @RequestBody ProductOptionUpdateRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(optionFacade.update(loginUserId, optionId, request));
    }

    /** option-02 : 옵션 삭제 */
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> delete(@PathVariable Long optionId,
                                       @RequestAttribute Long loginUserId) {
        optionFacade.delete(loginUserId, optionId);
        return ResponseEntity.noContent().build();
    }
}
