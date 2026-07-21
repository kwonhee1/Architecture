package com.example.shop.domain.user.service.usecase;

import com.example.shop.domain.user.dto.ChargePointRequest;
import com.example.shop.domain.user.dto.UserInfoResponse;
import com.example.shop.domain.user.dto.UserLoginRequest;
import com.example.shop.domain.user.dto.UserSignupRequest;

/**
 * controller ↔ user service 계약.
 * <p>
 * 여기 있는 함수는 controller 만 호출한다. 다른 domain 은 이 interface 를 주입받지 않으므로
 * 응답용 dto 를 만드는 함수를 가져다 쓸 수 없다.
 */
public interface UserUseCase {

    /** user-01 : 회원 가입 (가입 쿠폰 자동 발급) */
    void signup(UserSignupRequest request);

    /** user-02 : 로그인 → 쿠키에 넣을 userId 반환 */
    Long login(UserLoginRequest request);

    /** user-03 : 회원 정보 조회 */
    UserInfoResponse getMyInfo(Long userId);

    /** user-04 : 포인트 충전 */
    UserInfoResponse chargePoint(Long userId, ChargePointRequest request);
}
