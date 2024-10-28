package com.jygoh.anytime.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private static Key key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getUrlDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private TokenUtils() {
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Check for token in cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue(); // Return cookie value
                }
            }
        }

        return null;
    }

    public static Long getMemberIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

            // "memberId" 값을 String으로 가져옵니다
            String encryptedMemberId = claims.get("memberId", String.class);
            if (encryptedMemberId == null) {
                throw new IllegalArgumentException("User ID in token cannot be null or empty");
            }

            // 암호화된 memberId를 복호화한 후 Long 타입으로 변환합니다
            String decryptedMemberId = EncryptionUtils.decrypt(encryptedMemberId);
            return Long.parseLong(decryptedMemberId);

        } catch (JwtException e) {
            // JWT 예외 처리
            throw new IllegalArgumentException("Invalid JWT token", e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }
}
