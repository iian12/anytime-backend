package com.jygoh.anytime.global.handler;

import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.auth.service.CustomUserDetail;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberService;

    public CustomOAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider,
        MemberRepository memberService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        Long memberId = userDetail.getMemberId();

        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userDetail.getMemberId());
        Cookie accessTokenCookie = createCookie("access_token", accessToken);
        Cookie refreshTokenCookie = createCookie("refresh_token", refreshToken);
        // 쿠키를 응답에 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
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
