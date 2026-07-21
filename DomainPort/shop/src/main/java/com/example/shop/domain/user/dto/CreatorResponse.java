package com.example.shop.domain.user.dto;

/**
 * 응답에 실리는 사용자 { name }.
 * <p>
 * 상품의 생성자 자리(product-01/02/03/04/05)와 주문의 주문자 자리(order-01)가 같은 모양이라
 * 둘이 함께 쓴다. 사용자 정보의 모양이므로 user 가 소유한다.
 */
public record CreatorResponse(String name) {}
