package com.jygoh.anytime.domain.member.dto;

import com.jygoh.anytime.domain.member.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterReqDto {

    private String email;
    private String password;
    private String nickname;

    @Builder
    public RegisterReqDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toEntity() {
        return Member.builder().email(this.email).password(this.password).nickname(this.nickname)
            .build();
    }
}
