package com.jygoh.anytime.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleUserDto {

    private String email;
    private String nickname;
    private String profileImageUrl;
    private String subjectId;
}
