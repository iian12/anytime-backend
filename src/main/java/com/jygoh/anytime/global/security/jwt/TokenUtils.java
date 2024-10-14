package com.jygoh.anytime.global.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class TokenUtils {

    private TokenUtils() {
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // 쿠키가 존재하는지 확인
        if (cookies != null) {
            // 모든 쿠키를 순회하면서 accessToken 쿠키를 찾음
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue(); // accessToken의 값 반환
                }
            }
        }
        return null;
    }
}
