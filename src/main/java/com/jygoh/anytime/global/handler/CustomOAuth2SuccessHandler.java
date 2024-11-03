package com.jygoh.anytime.global.handler;

import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.auth.service.CustomUserDetail;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
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

        // 프로필 ID가 존재하는지 확인
        if (userDetail.getProfileId() == null) {
            response.sendError(HttpStatus.PRECONDITION_REQUIRED.value(), "Set ProfileID");
        } else {
            // 프로필 ID가 없을 경우 별도의 처리 (예: 오류 응답 반환)
            String accessToken = jwtTokenProvider.createAccessToken(memberId);
            String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

            Cookie accessTokenCookie = createCookie("access_token", accessToken);
            Cookie refreshTokenCookie = createCookie("refresh_token", refreshToken);

            // 쿠키를 응답에 추가
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
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
