package com.example.shop.domain.user.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.dto.ChargePointRequest;
import com.example.shop.domain.user.dto.UserInfoResponse;
import com.example.shop.domain.user.dto.UserLoginRequest;
import com.example.shop.domain.user.dto.UserSignupRequest;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.UserRepository;
import com.example.shop.domain.user.service.port.UserDomainPort;
import com.example.shop.domain.user.service.usecase.UserUseCase;
import com.example.shop.domain.user.service.vo.UserVo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * user domain 의 로직 소유자.
 * <p>
 * controller 를 향해서는 {@link UserUseCase}, 다른 domain 을 향해서는 {@link UserDomainPort}
 * 두 얼굴을 갖지만, 호출자는 자기 얼굴만 본다.
 * <p>
 * CouponService 는 같은 domain 이므로 port 없이 직접 호출한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserUseCase, UserDomainPort {

    private final UserRepository userRepository;
    private final CouponService couponService;

    // ────────────── UseCase (controller 전용) ──────────────

    /** user-01 : 회원 가입 (email 중복 확인, 가입 후 회원 가입 쿠폰 자동 발급) */
    @Override
    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = userRepository.save(User.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .build());

        couponService.issueSignupCoupon(user);
    }

    /** user-02 : 로그인 */
    @Override
    public Long login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
        if (!user.matchPassword(request.password())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        return user.getId();
    }

    /** user-03 : 회원 정보 조회 */
    @Override
    public UserInfoResponse getMyInfo(Long userId) {
        return UserInfoResponse.from(getEntity(userId));
    }

    /** user-04 : 포인트 충전 */
    @Override
    @Transactional
    public UserInfoResponse chargePoint(Long userId, ChargePointRequest request) {
        User user = getEntity(userId);
        user.addPoint(request.point());
        return UserInfoResponse.from(user);
    }

    // ────────────── DomainPort (타 domain 전용) ──────────────

    @Override
    public UserInfo getUserInfo(Long userId) {
        return UserInfo.of(getEntity(userId));
    }

    @Override
    public List<UserInfo> getUserInfos(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return userRepository.findByIdIn(userIds).stream()
                .map(UserInfo::of)
                .toList();
    }

    /** 포인트가 충분한지는 user 가 판단한다. 부족하면 예외 → 호출한 트랜잭션 전체 롤백. */
    @Override
    @Transactional
    public PointUseResult usePoint(Long userId, long amount) {
        User user = getEntity(userId);
        user.usePoint(amount);
        return new PointUseResult(UserInfo.of(user), amount, user.getPoint());
    }

    @Override
    @Transactional
    public PointRefundResult refundPoint(Long userId, long amount) {
        User user = getEntity(userId);
        user.refundPoint(amount);
        return new PointRefundResult(UserInfo.of(user), amount, user.getPoint());
    }

    // ────────────── 내부 ──────────────

    /** entity 는 domain 밖으로 나가지 않으므로 private 이다. */
    private User getEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
