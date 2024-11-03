package com.jygoh.anytime.global.security.auth.service;

import com.jygoh.anytime.domain.member.dto.LoginReqDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import com.jygoh.anytime.global.security.jwt.dto.NewAccessTokenResDto;
import com.jygoh.anytime.global.security.jwt.model.RefreshToken;
import com.jygoh.anytime.global.security.jwt.repository.RefreshTokenRepository;
import com.jygoh.anytime.global.security.jwt.dto.TokenResponseDto;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AuthServiceImpl(JwtTokenProvider jwtTokenProvider,
        RefreshTokenRepository refreshTokenRepository, MemberRepository memberService,
        BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public TokenResponseDto login(LoginReqDto reqDto) {
        // 사용자 존재 여부 확인
        Member member = memberService.findByEmail(reqDto.getEmail())
            .orElseThrow(() -> new BadCredentialsException("ID 또는 비밀번호가 잘못되었습니다."));

        // 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(reqDto.getEmail());
        // 비밀번호 검증
        if (passwordEncoder.matches(reqDto.getPassword(), userDetails.getPassword())) {
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

            // TokenResponseDto 반환
            TokenResponseDto tokenResponseDto = new TokenResponseDto();
            tokenResponseDto.setAccessToken(accessToken);
            tokenResponseDto.setRefreshToken(refreshToken);
            return tokenResponseDto;
        } else {
            throw new BadCredentialsException("ID 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @Override
    public NewAccessTokenResDto refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Long memberId = TokenUtils.getMemberIdFromToken(refreshToken);
        RefreshToken existingRefreshToken = refreshTokenRepository.findByMemberId(memberId)
            .orElseThrow(
                () -> new IllegalArgumentException("Refresh token does not exist or is invalid"));
        if (!existingRefreshToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token does not match");
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        // TokenResponseDto 객체 생성
        NewAccessTokenResDto accessTokenResDto = new NewAccessTokenResDto();
        accessTokenResDto.setAccessToken(newAccessToken);
        return accessTokenResDto;
    }
}
