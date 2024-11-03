package com.jygoh.anytime.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageDto {

    private Long id;
    private String content;
    private LocalDateTime timeStamp;
    private boolean isRead;


    @Builder
    public ChatMessageDto(Long id, String content, LocalDateTime timeStamp, boolean isRead) {
        this.id = id;
        this.content = content;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
    }
}
