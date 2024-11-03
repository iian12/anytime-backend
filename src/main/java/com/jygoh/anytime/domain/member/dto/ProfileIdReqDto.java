package com.jygoh.anytime.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileIdReqDto {

    private String profileId;

    @Builder
    public ProfileIdReqDto(String profileId) {
        this.profileId = profileId;
    }
}
