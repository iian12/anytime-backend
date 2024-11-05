package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.ProfileResDto;

public interface ProfileService {

    ProfileResDto getProfile(String profileId, String token);

    String getMyProfileImgUrl(String token);
}
