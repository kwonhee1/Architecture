package com.example.shop.domain.product.dto;

/**
 * order-01 : 구매 처리 결과. product domain 이 order domain 에 넘기는 VO.
 * 재고 차감과 금액 계산이 끝난 상태이므로, order 는 이 값만 가지고 주문을 만든다.
 */
public record PurchaseResult(
        Long productId,
        Long optionId,
        int count,
        int purchasePrice
) {}
