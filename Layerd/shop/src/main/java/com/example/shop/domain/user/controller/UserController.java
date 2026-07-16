package com.example.shop.domain.user.controller;

import com.example.shop.domain.user.dto.ChargePointRequest;
import com.example.shop.domain.user.dto.UserLoginRequest;
import com.example.shop.domain.user.dto.UserInfo;
import com.example.shop.domain.user.dto.UserSignupRequest;
import com.example.shop.common.auth.AuthFilter;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final UserService userService;

    /** user-01 : 회원 가입 */
    @PostMapping("/register")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** user-02 : 로그인 (UserId 쿠키 발급) */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody UserLoginRequest request,
                                      HttpServletResponse response) {
        User user = userService.login(request);
        Cookie cookie = new Cookie(USER_ID_COOKIE, String.valueOf(user.getId()));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    /** user-03 : 회원 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(@RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(UserInfo.from(userService.getUser(loginUserId)));
    }

    /** user-04 : 포인트 충전 */
    @PostMapping("/me/points")
    public ResponseEntity<UserInfo> chargePoint(@Valid @RequestBody ChargePointRequest request,
                                                @RequestAttribute Long loginUserId) {
        return ResponseEntity.ok(UserInfo.from(userService.chargePoint(loginUserId, request.point())));
    }
}
