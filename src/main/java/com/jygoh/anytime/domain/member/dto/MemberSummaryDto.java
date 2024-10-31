package com.jygoh.anytime.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSummaryDto {

    private String profileId;
    private String profileImgUrl;
    private String nickname;
    private boolean isMutualFollow;
    private boolean canSendMessage;

    @Builder
    public MemberSummaryDto(String profileId, String profileImgUrl, String nickname,
        boolean isMutualFollow, boolean canSendMessage) {
        this.profileId = profileId;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
        this.isMutualFollow = isMutualFollow;
        this.canSendMessage = canSendMessage;
    }
}
