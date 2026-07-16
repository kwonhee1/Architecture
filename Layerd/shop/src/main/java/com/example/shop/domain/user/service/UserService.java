package com.example.shop.domain.user.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.coupon.service.CouponService;
import com.example.shop.domain.user.dto.UserLoginRequest;
import com.example.shop.domain.user.dto.UserSignupRequest;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CouponService couponService;

    /** user-01 : 회원 가입 (email 중복 확인, 가입 후 회원 가입 쿠폰 자동 발급) */
    @Transactional
    public User signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = userRepository.save(User.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .build());

        couponService.issueSignupCoupon(user);

        return user;
    }

    /** user-02 : 로그인 */
    public User login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
        if (!user.getPassword().equals(request.password())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        return user;
    }

    /** user-03 : 회원 정보 조회 */
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /** user-04 : 포인트 충전 */
    @Transactional
    public User chargePoint(Long userId, long amount) {
        User user = getUser(userId);

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_CHARGE_POINT);
        }

        user.addPoint(amount);
        return user;
    }
}
