package com.example.shop.product.domain.model.vo;

/** 상품 생성자(회원) 식별자. user aggregate 를 객체가 아니라 식별자로 참조한다. */
public record CreatorId(Long value) {

    public CreatorId {
        if (value == null) {
            throw new IllegalArgumentException("CreatorId 는 null 일 수 없습니다.");
        }
    }

    public static CreatorId of(Long value) {
        return new CreatorId(value);
    }
}
