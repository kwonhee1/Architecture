package com.example.shop.coupon.domain.repository;

import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.model.vo.OwnerId;

import java.util.List;
import java.util.Optional;

/**
 * coupon aggregate 저장/조회 계약.
 * interface 는 domain 에, 구현은 infrastructure 에 둔다.
 */
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(CouponId id);

    /** coupon-02 : 소유자의 사용 가능한(미사용) 쿠폰만 조회. */
    List<Coupon> findAvailableByOwner(OwnerId ownerId);
}
