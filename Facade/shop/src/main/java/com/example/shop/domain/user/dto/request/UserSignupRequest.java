package com.example.shop.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** user-01 : 회원 가입 요청 */
public record UserSignupRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name
) {}
