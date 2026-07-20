package com.example.shop.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** user-02 : 로그인 요청 */
public record UserLoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
