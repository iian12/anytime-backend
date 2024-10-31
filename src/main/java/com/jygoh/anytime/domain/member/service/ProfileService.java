package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.MemberSummaryDto;
import com.jygoh.anytime.domain.member.dto.ProfileResDto;
import java.util.List;

public interface ProfileService {

    ProfileResDto getProfile(String profileId, String token);
}
