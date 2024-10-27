package com.jygoh.anytime.global.security.auth;

import com.jygoh.anytime.domain.member.dto.LoginReqDto;
import com.jygoh.anytime.global.security.jwt.NewAccessTokenResDto;
import com.jygoh.anytime.global.security.jwt.TokenResponseDto;

public interface AuthService {

    TokenResponseDto login(LoginReqDto reqDto);

    NewAccessTokenResDto refreshToken(String refreshToken);

}
