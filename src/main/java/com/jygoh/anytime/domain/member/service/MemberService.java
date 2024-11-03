package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.ProfileIdDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.global.security.jwt.dto.TokenResponseDto;

public interface MemberService {

    void register(RegisterReqDto reqDto);

    TokenResponseDto processingGoogleUser(GoogleUserDto dto);

    TokenResponseDto setProfileId(ProfileIdDto profileIdDto);

}
