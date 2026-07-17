package com.example.shop.domain.user.facade;

import com.example.shop.domain.user.dto.request.ChargePointRequest;
import com.example.shop.domain.user.dto.request.UserLoginRequest;
import com.example.shop.domain.user.dto.request.UserSignupRequest;
import com.example.shop.domain.user.dto.response.UserResponse;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * user application service.
 * 실행 순서와 트랜잭션 경계만 책임지고, domain 로직은 UserService 에게 시킨다.
 */
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    /** user-01 : 회원 가입 (가입 쿠폰 발급까지 한 트랜잭션) */
    @Transactional
    public void signup(UserSignupRequest request) {
        userService.signup(request.email(), request.password(), request.name());
    }

    /** user-02 : 로그인 → 쿠키에 담을 userId 반환 */
    @Transactional(readOnly = true)
    public Long login(UserLoginRequest request) {
        return userService.login(request.email(), request.password()).getId();
    }

    /** user-03 : 회원 정보 조회 */
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        return userService.getUser(userId).toUserResponse();
    }

    /** user-04 : 포인트 충전 */
    @Transactional
    public UserResponse chargePoint(Long userId, ChargePointRequest request) {
        return userService.chargePoint(userId, request.point()).toUserResponse();
    }
}
