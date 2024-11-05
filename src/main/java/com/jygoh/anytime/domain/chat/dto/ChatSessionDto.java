package com.jygoh.anytime.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatSessionDto {

    private String id;

    @Builder
    public ChatSessionDto(String id) {
        this.id = id;
    }
}
