package com.jygoh.anytime.domain.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatSessionDto {

    private Long id;
    private String member1Nickname;
    private String member2ProfileId; // 상대방의 프로필 ID
    private String member2Nickname; // 상대방의 닉네임
    private String member2ProfileImageUrl;
    private List<ChatMessageDto> messages;

    @Builder
    public ChatSessionDto(Long id, String member1Nickname, String member2ProfileId,
        String member2Nickname, String member2ProfileImageUrl, List<ChatMessageDto> messages) {
        this.id = id;
        this.member1Nickname = member1Nickname;
        this.member2ProfileId = member2ProfileId;
        this.member2Nickname = member2Nickname;
        this.member2ProfileImageUrl = member2ProfileImageUrl;
        this.messages = messages;
    }
}
