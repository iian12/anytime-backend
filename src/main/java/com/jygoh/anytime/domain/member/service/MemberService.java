package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.model.Member;

public interface MemberService {

    void register(RegisterReqDto reqDto);

    Member processingGoogleUser(GoogleUserDto dto);
}
