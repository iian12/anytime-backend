package com.jygoh.anytime.global.handler;

import com.jygoh.anytime.global.security.auth.service.CustomUserDetail;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public CustomOAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        Long memberId = userDetail.getMemberId();
        log.info(userDetail.getMember().getProfileId());
        // 프로필 ID가 존재하는지 확인
        if (userDetail.getMember().getProfileId() == null) {
            response.sendRedirect("http://localhost:3000/set-profileId");
        } else {
            String accessToken = jwtTokenProvider.createAccessToken(memberId);
            String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

            Cookie accessTokenCookie = createCookie("access_token", accessToken);
            Cookie refreshTokenCookie = createCookie("refresh_token", refreshToken);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            response.sendRedirect("http://localhost:3000/login/callback");
        }
    }

    private Cookie createCookie(String name, String token) {
        if (name.equals("accessToken")) {
            Cookie cookie = new Cookie(name, token);
            cookie.setHttpOnly(false);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 2);
            return cookie;
        } else {
            Cookie cookie = new Cookie(name, token);
            cookie.setHttpOnly(false);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 14);
            return cookie;
        }
    }
}
