package com.jygoh.anytime.domain.chat.dto;

import com.jygoh.anytime.domain.chat.model.PrivateChatMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateChatMessageRes {

    private String content;
    private LocalDateTime sentAt;
    private boolean isMine;

    @Builder
    public PrivateChatMessageRes(String content, LocalDateTime sentAt, boolean isMine) {
        this.content = content;
        this.sentAt = sentAt;
        this.isMine = isMine;
    }

    public static PrivateChatMessageRes from(PrivateChatMessage message, Long memberId) {
        return PrivateChatMessageRes.builder()
            .content(message.getContent())
            .sentAt(message.getSendAt())
            .isMine(message.getSender().getId().equals(memberId))
            .build();
    }
}
