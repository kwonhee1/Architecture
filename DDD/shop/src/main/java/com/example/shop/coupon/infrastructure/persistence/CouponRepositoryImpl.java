package com.example.shop.coupon.infrastructure.persistence;

import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.model.vo.OwnerId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** domain 의 CouponRepository 계약을 JPA 로 구현한다. */
@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity saved = jpaRepository.save(CouponMapper.toEntity(coupon));
        return CouponMapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(CouponId id) {
        return jpaRepository.findById(id.value()).map(CouponMapper::toDomain);
    }

    @Override
    public List<Coupon> findAvailableByOwner(OwnerId ownerId) {
        return jpaRepository.findByOwnerIdAndUsedFalse(ownerId.value()).stream()
                .map(CouponMapper::toDomain)
                .toList();
    }
}
