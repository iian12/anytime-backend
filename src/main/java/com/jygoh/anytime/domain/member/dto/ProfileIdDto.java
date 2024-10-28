package com.jygoh.anytime.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileIdDto {

    private String encodedMemberId;
    private String profileId;
}
