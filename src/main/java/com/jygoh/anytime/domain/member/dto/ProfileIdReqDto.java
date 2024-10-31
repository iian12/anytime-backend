package com.jygoh.anytime.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@NoArgsConstructor
public class ProfileIdReqDto {

    private String profileId;

    @Builder
    public ProfileIdReqDto(String profileId) {
        this.profileId = profileId;
    }
}
