package com.example.shop.domain.product.service.usecase;

import com.example.shop.domain.product.dto.ProductCreateRequest;
import com.example.shop.domain.product.dto.ProductDetailResponse;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.dto.ProductUpdateRequest;

import java.util.List;

/**
 * controller ↔ product service 계약.
 * <p>
 * controller 가 직접 호출하는 자리라 반환값은 전부 응답 dto 다. 경계 VO 는 port 쪽 언어이므로
 * 여기 나오지 않는다. order 는 이 interface 를 주입받지 않으므로 이 함수들을 호출해
 * Product 를 가져갈 수 없다.
 */
public interface ProductUseCase {

    /** product-01 : 새 상품 등록 (기본 옵션 함께 생성) */
    ProductResponse create(Long userId, ProductCreateRequest request);

    /** product-03 : 상품 리스트 조회 */
    List<ProductResponse> getAllProducts();

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    List<ProductResponse> getMyProducts(Long userId);

    /** product-04 : 상품 상세 조회 (옵션 포함) */
    ProductDetailResponse getProductDetail(Long productId);

    /** product-02 : 상품 정보 수정 */
    ProductResponse update(Long productId, Long userId, ProductUpdateRequest request);
}
