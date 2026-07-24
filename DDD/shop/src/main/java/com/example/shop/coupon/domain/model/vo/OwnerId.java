package com.example.shop.coupon.domain.model.vo;

/**
 * 쿠폰 소유자(회원) 식별자.
 *
 * <p>bounded context 간에는 모델을 공유하지 않는다. user context 의 UserId 와 별개로,
 * coupon context 는 자기 관점의 소유자 식별자를 따로 가진다. aggregate(user) 를 객체로
 * 참조하지 않고 식별자로만 참조한다.</p>
 */
public record OwnerId(Long value) {

    public OwnerId {
        if (value == null) {
            throw new IllegalArgumentException("OwnerId 는 null 일 수 없습니다.");
        }
    }

    public static OwnerId of(Long value) {
        return new OwnerId(value);
    }
}
