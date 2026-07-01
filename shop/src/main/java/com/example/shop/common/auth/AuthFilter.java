package com.example.shop.common.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthFilter implements Filter {

    private static final String USER_ID_COOKIE = "USER_ID";
    private static final List<String> ALWAYS_PUBLIC = List.of(
            "/api/users/login",
            "/api/users/signup"
    );
    private static final List<String> PUBLIC_GET_PATHS = List.of(
            "/api/products",
            "/api/products/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (isWhitelisted(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = extractUserId(httpRequest);
        if (userId == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        httpRequest.setAttribute("loginUserId", userId);
        chain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path, String method) {
        if (ALWAYS_PUBLIC.stream().anyMatch(p -> pathMatcher.match(p, path))) return true;
        if ("GET".equals(method) && PUBLIC_GET_PATHS.stream().anyMatch(p -> pathMatcher.match(p, path))) return true;
        return false;
    }

    private Long extractUserId(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> USER_ID_COOKIE.equals(c.getName()))
                .findFirst()
                .map(c -> {
                    try {
                        return Long.parseLong(c.getValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}
