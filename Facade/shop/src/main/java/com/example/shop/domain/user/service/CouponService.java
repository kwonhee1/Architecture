package com.example.shop.domain.user.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.entity.Coupon;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.CouponRepository;
import com.example.shop.domain.user.repository.UserRepository;
import com.example.shop.domain.user.service.vo.CouponResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * coupon domain service. coupon 은 user 와 같은 domain 이므로 UserService 와 서로 호출할 수 있다.
 * 다른 domain(product / order)의 service 는 호출하지 않는다.
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private static final String SIGNUP_COUPON_NAME = "회원 가입 쿠폰";
    private static final int SIGNUP_COUPON_AMOUNT = 1000;

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    /** coupon-01 : 회원 가입 시에만 자동 발급된다 (API 없음) → 같은 domain 의 UserService 만 호출 */
    void issueSignupCoupon(User user) {
        couponRepository.save(Coupon.builder()
                .user(user)
                .name(SIGNUP_COUPON_NAME)
                .discountAmount(SIGNUP_COUPON_AMOUNT)
                .build());
    }

    /** coupon-02 : 사용 가능한 쿠폰 리스트 조회 */
    public List<CouponInfo> getMyCoupons(Long userId) {
        return couponRepository.findByUserIdAndUsedFalse(userId).stream()
                .map(CouponInfo::from)
                .toList();
    }

    public List<CouponInfo> getCoupons(Collection<Long> couponIds) {
        return couponRepository.findByIdIn(couponIds).stream()
                .map(CouponInfo::from)
                .toList();
    }

    /**
     * order-01 : 쿠폰 사용. couponId 가 null 이면 쿠폰을 쓰지 않은 주문이므로 할인이 없다.
     * "쿠폰이 없으면 할인 0" 은 coupon 의 규칙이므로 facade 가 아니라 여기서 판단한다.
     */
    public CouponResult use(Long couponId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (couponId == null) {
            return CouponResult.none(user);
        }
        Coupon coupon = getEntity(couponId);
        int discountAmount = coupon.use(user);
        return CouponResult.from(user, coupon, discountAmount);
    }

    /** order-04 : 주문 취소 시 쿠폰 복구. 쿠폰을 쓰지 않은 주문이면 되돌릴 것이 없다. */
    public void restore(Long couponId) {
        if (couponId == null) {
            return;
        }
        getEntity(couponId).restore();
    }

    private Coupon getEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
    }
}
