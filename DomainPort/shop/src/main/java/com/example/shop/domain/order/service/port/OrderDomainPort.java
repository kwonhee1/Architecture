package com.example.shop.domain.order.service.port;

/**
 * 타 domain ↔ order service 계약.
 * <p>
 * product-02 / option-02 의 "이미 주문이 존재하면 수정·삭제 불가" 제약을 위해 product domain 이
 * 사용한다. product 가 order 의 repository 를 직접 뒤지지 않고 이 창구로 물어본다.
 */
public interface OrderDomainPort {

    /** 이 상품에 대한 주문이 있는지 (product-02 : 금액 수정 제약) */
    boolean existsByProduct(Long productId);

    /** 이 옵션에 대한 주문이 있는지 (option-02 : 설명/추가금액 수정·삭제 제약) */
    boolean existsByOption(Long optionId);
}
