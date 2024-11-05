package com.jygoh.anytime.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateChatResponse {

    private String chatId;
    private String requestId;
    private ChatStatus status;

    @Builder
    public PrivateChatResponse(String chatId, String requestId, ChatStatus status) {
        this.chatId = chatId;
        this.requestId = requestId;
        this.status = status;
    }

    public enum ChatStatus {
        PENDING_REQUEST,
        ERROR
    }
}
