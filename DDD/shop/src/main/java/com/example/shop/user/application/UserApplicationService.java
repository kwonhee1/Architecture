package com.example.shop.user.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.OwnerId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import com.example.shop.user.application.dto.UserInfo;
import com.example.shop.user.domain.model.User;
import com.example.shop.user.domain.model.vo.Email;
import com.example.shop.user.domain.model.vo.Password;
import com.example.shop.user.domain.model.vo.UserId;
import com.example.shop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * user use case 의 흐름을 조립한다 (실행 순서 · transaction 범위).
 *
 * <p>규칙(자격 증명 대조, 포인트 계산)은 domain 에게 시키고 그 결과를 저장 · 반환할 뿐이다.</p>
 *
 * <p>입력은 use case 에 필요한 값만 그대로 받는다. presentation 의 Request 를 받지 않으므로
 * 의존 방향은 지켜지고, Request 와 필드가 같은 Command 를 따로 두지도 않는다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    /**
     * user-01 : 회원 가입.
     * email 중복이면 실패하고, 가입 직후 첫 쿠폰 { "회원 가입 쿠폰", 1000 } 을 함께 발급한다.
     * User 와 Coupon 은 서로 다른 aggregate 지만 함께 성공/실패해야 하므로 한 트랜잭션으로 묶는다.
     */
    @Transactional
    public void register(String email, String password, String name) {
        Email registerEmail = Email.of(email);
        if (userRepository.existsByEmail(registerEmail)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.register(registerEmail, Password.of(password), name);
        User saved = userRepository.save(user);

        couponRepository.save(Coupon.issueSignupCoupon(OwnerId.of(saved.id().value())));
    }

    /** user-02 : 로그인. 성공하면 쿠키에 실을 userId 를 반환한다. */
    public long login(String email, String password) {
        Email loginEmail = Email.of(email);
        User user = userRepository.findByEmail(loginEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
        user.authenticate(loginEmail, Password.of(password));
        return user.id().value();
    }

    /** user-03 : 회원 정보 조회. */
    public UserInfo getMyInfo(long userId) {
        return UserInfo.from(loadUser(userId));
    }

    /** user-04 : 포인트 충전. */
    @Transactional
    public UserInfo chargePoint(long userId, long amount) {
        User user = loadUser(userId);
        user.chargePoint(amount);
        return UserInfo.from(userRepository.save(user));
    }

    private User loadUser(long userId) {
        return userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
