package com.example.shop.product.presentation;

import com.example.shop.product.application.OptionApplicationService;
import com.example.shop.product.presentation.dto.OptionCreateRequest;
import com.example.shop.product.presentation.dto.OptionResponse;
import com.example.shop.product.presentation.dto.OptionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OptionController {

    private final OptionApplicationService optionService;

    /** option-01 : 옵션 생성 */
    @PostMapping("/options/{productId}")
    public ResponseEntity<OptionResponse> create(@PathVariable Long productId,
                                                 @Valid @RequestBody OptionCreateRequest request,
                                                 @RequestAttribute Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OptionResponse.from(optionService.create(productId, loginUserId, request.toCommand())));
    }

    /** option-02 : 옵션 수정 */
    @PatchMapping("/options/{optionId}")
    public ResponseEntity<OptionResponse> update(@PathVariable Long optionId,
                                                 @RequestBody OptionUpdateRequest request,
                                                 @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(
                OptionResponse.from(optionService.update(optionId, loginUserId, request.toCommand())));
    }

    /** option-02 : 옵션 삭제 */
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> delete(@PathVariable Long optionId,
                                       @RequestAttribute Long loginUserId) {
        optionService.delete(optionId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
