package com.jygoh.anytime.domain.chat.dto;

import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.model.PrivateChat;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomResponse {

    private static final EncodeDecode encodeDecode = new EncodeDecode();

    private String id;
    private String title;
    private String requestId;
    private List<String> memberProfileIds;

    @Builder
    public ChatRoomResponse(String id, String title, String requestId, List<String> memberProfileIds) {
        this.id = id;
        this.title = title;
        this.requestId = requestId;
        this.memberProfileIds = memberProfileIds;
    }

    public static List<ChatRoomResponse> fromChats(List<GroupChat> groupChats,
        List<PrivateChat> privateChats, Long currentMemberId) {

        List<ChatRoomResponse> groupChatResponses = groupChats.stream()
            .map(chat -> ChatRoomResponse.builder()
                .id(encodeDecode.encode(chat.getId()))
                .title(chat.getTitle())
                .memberProfileIds(chat.getMembers().stream()
                    .map(member -> member.getMember().getProfileId())  // ProfileId 사용
                    .collect(Collectors.toList()))
                .build())
            .toList();

        List<ChatRoomResponse> privateChatResponses = privateChats.stream()
            .map(chat -> ChatRoomResponse.builder()
                .id(encodeDecode.encode(chat.getId()))
                .title(null) // 개인 채팅은 제목이 없으므로 null
                .memberProfileIds(List.of(
                    chat.getMember1().getId().equals(currentMemberId)
                        ? chat.getMember2().getProfileId()
                        : chat.getMember1().getProfileId()))
                .build())
            .toList();

        List<ChatRoomResponse> combinedResponses = new ArrayList<>(groupChatResponses);
        combinedResponses.addAll(privateChatResponses);
        return combinedResponses;
    }
}
