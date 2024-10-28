package com.jygoh.anytime.global.security.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class IdTokenDto {

    private String idToken;

    @Builder
    public IdTokenDto(String idToken) {
        this.idToken = idToken;
    }
}
