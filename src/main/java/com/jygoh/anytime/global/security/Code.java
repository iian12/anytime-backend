package com.jygoh.anytime.global.security;

import java.util.Base64;

public class Code {

    public String encodeTeamId(Long teamId) {
        return Base64.getEncoder().encodeToString(teamId.toString().getBytes());
    }

    public Long decodeTeamId(String encodeTeamId) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodeTeamId);
        return Long.parseLong(new String(decodedBytes));
    }

}
