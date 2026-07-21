package com.example.shop.domain.product.service.port;

import com.example.shop.domain.product.service.vo.ProductVo.*;

import java.util.List;

/**
 * 타 domain ↔ product service 계약.
 * <p>
 * Product 를 그대로 내주지 않는다. 내주면 order 가 product 의 규칙(금액 계산·소유자 판단)을
 * 대신 판단하게 되기 때문이다.
 */
public interface ProductDomainPort {

    /** 상품 정보 일괄 조회 (order-02 응답의 상품 정보 용) */
    List<ProductInfo> getProductInfos(List<Long> productIds);

    /** 이 상품의 생성자(판매자)가 맞는지는 product 가 판단한다 (order-03 판매자 조회 용) */
    boolean checkOwner(Long productId, Long userId);
}
