package com.jygoh.anytime.domain.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupChatResponse {

    private String chatId;
    private List<String> memberProfileIds;

    @Builder
    public GroupChatResponse(String chatId, List<String> memberProfileIds) {
        this.chatId = chatId;
        this.memberProfileIds = memberProfileIds;
    }
}
