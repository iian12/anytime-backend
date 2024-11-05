package com.jygoh.anytime.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageDto {

    private String id;
    private String content;
    private LocalDateTime timeStamp;

    @Builder
    public ChatMessageDto(String id, String content, LocalDateTime timeStamp) {
        this.id = id;
        this.content = content;
        this.timeStamp = timeStamp;
    }
}
