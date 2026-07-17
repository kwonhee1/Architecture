package com.example.shop.common.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AuthFilter implements Filter {

    /** 로그인 시 발급되는 쿠키 이름 (shopping-api.yaml: UserId=%d) */
    private static final String USER_ID_COOKIE = "UserId";

    /**
     * 인증 없이 접근 가능한 경로 레지스트리.
     * 필터가 경로를 직접 알지 않고, 각 컨트롤러가 자신의 공개 엔드포인트를
     * static 블록에서 {@link #registerPublic} 로 등록한다.
     */
    private static final Set<PublicEndpoint> PUBLIC_ENDPOINTS = ConcurrentHashMap.newKeySet();

    /** 공개 경로 등록. pathRegex 는 전체 매칭 정규식(고정 경로 문자열도 그대로 사용 가능). */
    public static void registerPublic(String method, String pathRegex) {
        PUBLIC_ENDPOINTS.add(new PublicEndpoint(method, Pattern.compile(pathRegex)));
    }

    private record PublicEndpoint(String method, Pattern pathPattern) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (isPublic(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = extractUserId(httpRequest);
        if (userId == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        httpRequest.setAttribute("loginUserId", userId);
        chain.doFilter(request, response);
    }

    /** 컨트롤러가 등록한 공개 경로에 해당하는지 확인 */
    private boolean isPublic(String path, String method) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(e -> e.method().equals(method) && e.pathPattern().matcher(path).matches());
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
