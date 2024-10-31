package com.jygoh.anytime.domain.follow.service;

import com.jygoh.anytime.domain.member.dto.MemberSummaryDto;
import java.util.List;

public interface FollowService {

    List<MemberSummaryDto> getFollowingList(String profileId, String token);

    List<MemberSummaryDto> getFollowerList(String profileId, String token);

    String toggleFollow(String targetProfileId, String token);

    void deleteFollower(String targetProfileId, String token);

    String acceptFollowRequest(String targetProfileId, String token);

    String rejectFollowRequest(String targetProfileId, String token);
}
