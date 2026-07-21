package com.example.shop.domain.user.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.dto.CouponResponse;
import com.example.shop.domain.user.entity.Coupon;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.CouponRepository;
import com.example.shop.domain.user.service.port.CouponDomainPort;
import com.example.shop.domain.user.service.usecase.CouponUseCase;
import com.example.shop.domain.user.service.vo.CouponVo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * coupon 의 로직 소유자.
 * <p>
 * coupon 은 user 와 같은 domain 이므로 {@link UserService} 와는 port 없이 직접 오간다.
 * 다른 domain(order) 은 {@link CouponDomainPort} 로만 접근한다.
 * <p>
 * controller 로는 dto, 다른 domain 으로는 VO 를 내보낸다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService implements CouponUseCase, CouponDomainPort {

    private static final String SIGNUP_COUPON_NAME = "회원 가입 쿠폰";
    private static final int SIGNUP_COUPON_AMOUNT = 1000;

    private final CouponRepository couponRepository;

    // ────────────── UseCase (controller 전용) ──────────────

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회 */
    @Override
    public List<CouponResponse> getMyCoupons(Long userId) {
        return couponRepository.findByUserIdAndUsedFalse(userId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    // ────────────── 같은 domain(user) 전용 ──────────────

    /**
     * coupon-01 : 회원 가입 시 자동 발급되는 쿠폰 (별도 API 없음).
     * user 와 같은 domain 이므로 User entity 를 그대로 받는다.
     */
    @Transactional
    public void issueSignupCoupon(User user) {
        couponRepository.save(Coupon.builder()
                .user(user)
                .name(SIGNUP_COUPON_NAME)
                .discountAmount(SIGNUP_COUPON_AMOUNT)
                .build());
    }

    // ────────────── DomainPort (타 domain 전용) ──────────────

    @Override
    public List<CouponInfo> getCouponInfos(List<Long> couponIds) {
        if (couponIds == null || couponIds.isEmpty()) {
            return List.of();
        }
        return couponRepository.findAllById(couponIds).stream()
                .map(CouponInfo::of)
                .toList();
    }

    /**
     * 쿠폰이 내 것인지·이미 썼는지는 coupon 이 판단한다. 실패 시 예외 → 호출 트랜잭션 롤백.
     * <p>
     * 쿠폰은 선택이므로 couponId 가 null 이면 "쓴 쿠폰 없음 · 할인 0" 을 결과로 돌려준다.
     * 호출자가 쿠폰 유무를 먼저 따져 분기하지 않아도 되게 하는 것도 coupon 의 몫이다.
     */
    @Override
    @Transactional
    public CouponUseResult useCoupon(Long couponId, Long userId) {
        if (couponId == null) {
            return CouponUseResult.none();
        }
        Coupon coupon = getEntity(couponId);
        int discountAmount = coupon.use(userId);
        return new CouponUseResult(CouponInfo.of(coupon), discountAmount);
    }

    /** 취소 시 되돌린다. 쓴 쿠폰이 없던 주문이면 couponId 가 null 로 들어온다. */
    @Override
    @Transactional
    public CouponRestoreResult restoreCoupon(Long couponId) {
        if (couponId == null) {
            return CouponRestoreResult.none();
        }
        Coupon coupon = getEntity(couponId);
        coupon.restore();
        return new CouponRestoreResult(CouponInfo.of(coupon), !coupon.isUsed());
    }

    // ────────────── 내부 ──────────────

    private Coupon getEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
    }

}
