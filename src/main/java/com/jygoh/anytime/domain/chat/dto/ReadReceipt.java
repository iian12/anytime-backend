package com.jygoh.anytime.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadReceipt {

    private String messageId;
    private boolean isRead;
    private LocalDateTime readAt;

    @Builder
    public ReadReceipt(String messageId, boolean isRead, LocalDateTime readAt) {
        this.messageId = messageId;
        this.isRead = isRead;
        this.readAt = readAt;
    }
}
