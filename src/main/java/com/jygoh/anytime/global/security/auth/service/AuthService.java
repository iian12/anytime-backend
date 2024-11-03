package com.jygoh.anytime.global.security.auth.service;

import com.jygoh.anytime.domain.member.dto.LoginReqDto;
import com.jygoh.anytime.global.security.jwt.dto.NewAccessTokenResDto;
import com.jygoh.anytime.global.security.jwt.dto.TokenResponseDto;

public interface AuthService {

    TokenResponseDto login(LoginReqDto reqDto);

    NewAccessTokenResDto refreshToken(String refreshToken);

}
