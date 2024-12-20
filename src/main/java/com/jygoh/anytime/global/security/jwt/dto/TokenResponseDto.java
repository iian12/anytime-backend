package com.jygoh.anytime.global.security.jwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
    private String encodedMemberId;
}
