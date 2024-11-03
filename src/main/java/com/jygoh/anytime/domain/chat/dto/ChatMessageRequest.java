package com.jygoh.anytime.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    private String targetProfileId;
    private String messageContent;

    @Builder
    public ChatMessageRequest(String targetProfileId, String messageContent) {
        this.targetProfileId = targetProfileId;
        this.messageContent = messageContent;
    }
}
