package com.example.shop.domain.product.service.usecase;

import com.example.shop.domain.product.dto.ProductOptionCreateRequest;
import com.example.shop.domain.product.dto.ProductOptionResponse;
import com.example.shop.domain.product.dto.ProductOptionUpdateRequest;

/**
 * controller ↔ option service 계약.
 * <p>
 * controller 가 직접 호출하는 자리라 반환값은 응답 dto 다 (경계 VO 는 port 쪽 언어다).
 */
public interface ProductOptionUseCase {

    /** option-01 : 옵션 생성 */
    ProductOptionResponse create(Long productId, Long userId, ProductOptionCreateRequest request);

    /** option-02 : 옵션 수정 */
    ProductOptionResponse update(Long optionId, Long userId, ProductOptionUpdateRequest request);

    /** option-02 : 옵션 삭제 */
    void delete(Long optionId, Long userId);
}
