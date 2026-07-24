package com.example.shop.user.presentation;

import com.example.shop.common.auth.AuthFilter;
import com.example.shop.user.application.UserApplicationService;
import com.example.shop.user.presentation.dto.ChargePointRequest;
import com.example.shop.user.presentation.dto.LoginRequest;
import com.example.shop.user.presentation.dto.SignUpRequest;
import com.example.shop.user.presentation.dto.UserInfoResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 요청/응답의 변환만 담당한다. domain 객체를 노출하지 않고 DTO 로 주고받는다.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String USER_ID_COOKIE = "UserId";

    /** 인증 없이 접근 가능한 공개 경로 등록 (회원가입 / 로그인) */
    static {
        AuthFilter.registerPublic("POST", "/users/register");
        AuthFilter.registerPublic("POST", "/users/login");
    }

    private final UserApplicationService userService;

    /** user-01 : 회원 가입 */
    @PostMapping("/register")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest request) {
        userService.register(request.email(), request.password(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** user-02 : 로그인 (UserId 쿠키 발급) */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
                                      HttpServletResponse response) {
        long userId = userService.login(request.email(), request.password());
        Cookie cookie = new Cookie(USER_ID_COOKIE, String.valueOf(userId));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    /** user-03 : 회원 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(UserInfoResponse.from(userService.getMyInfo(loginUserId)));
    }

    /** user-04 : 포인트 충전 */
    @PostMapping("/me/points")
    public ResponseEntity<UserInfoResponse> chargePoint(@Valid @RequestBody ChargePointRequest request,
                                                        @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(
                UserInfoResponse.from(userService.chargePoint(loginUserId, request.point())));
    }
}
