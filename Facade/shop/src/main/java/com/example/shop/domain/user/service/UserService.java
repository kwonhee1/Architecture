package com.example.shop.domain.user.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.entity.UserInfo;
import com.example.shop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * user domain service. user domain 의 로직만 담당하며,
 * 다른 domain(product / order)의 service 는 호출하지 않는다.
 * CouponService 는 같은 domain(user, coupon) 이므로 호출할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CouponService couponService;

    /** user-01 : 회원 가입 (email 중복 확인 → 가입 → 가입 쿠폰 발급) */
    public User signup(String email, String password,String name) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = userRepository.save(User.builder()
                .email(email)
                .password(password)
                .name(name)
                .build());

        couponService.issueSignupCoupon(user);
        return user;
    }

    /** user-02 : 로그인 */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
        if (!user.matchPassword(password)) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        return user;
    }

    public UserInfo getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserInfo.from(user);
    }

    public List<UserInfo> getUsers(Collection<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(UserInfo::from)
                .toList();
    }

    /** user-04 : 포인트 충전 */
    public UserInfo chargePoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.chargePoint(amount);
        return UserInfo.from(userRepository.save(user));
    }

    /** order-01 : 주문 금액만큼 포인트 차감 (부족 시 예외 → facade 의 트랜잭션이 전체 롤백) */
    public void usePoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.usePoint(amount);
        userRepository.save(user);
    }

    /** order-04 : 주문 취소 시 포인트 복원 */
    public void refundPoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.refundPoint(amount);
        userRepository.save(user);
    }
}
