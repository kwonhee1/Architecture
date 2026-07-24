package com.example.shop.product.application.port;

/**
 * product/option 이 "이 상품·옵션에 대한 주문이 존재하는가" 를 알기 위한 계약.
 *
 * <p>주문 존재 여부는 order aggregate 의 사실이지만, product 는 order 의 domain 모델을
 * 알 필요가 없다. 그래서 product 가 <b>필요로 하는 만큼만</b> 원시값 계약으로 정의하고
 * (consumer-driven), 구현(adapter)은 order context 가 제공한다.</p>
 */
public interface OrderExistencePort {

    /** product-02 : 이 상품에 대한 주문이 존재하는가. */
    boolean existsForProduct(long productId);

    /** option-02 : 이 옵션에 대한 주문이 존재하는가. */
    boolean existsForOption(long optionId);
}
