package com.example.shop.coupon.infrastructure.persistence;

import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.model.vo.Money;
import com.example.shop.coupon.domain.model.vo.OwnerId;

/** domain Coupon <-> CouponJpaEntity 변환. */
final class CouponMapper {

    private CouponMapper() {
    }

    static CouponJpaEntity toEntity(Coupon coupon) {
        Long id = coupon.id() == null ? null : coupon.id().value();
        return new CouponJpaEntity(
                id,
                coupon.ownerId().value(),
                coupon.name(),
                coupon.discount().value(),
                coupon.isUsed()
        );
    }

    static Coupon toDomain(CouponJpaEntity entity) {
        return Coupon.reconstitute(
                CouponId.of(entity.getId()),
                OwnerId.of(entity.getOwnerId()),
                entity.getName(),
                Money.of(entity.getDiscountAmount()),
                entity.isUsed()
        );
    }
}
