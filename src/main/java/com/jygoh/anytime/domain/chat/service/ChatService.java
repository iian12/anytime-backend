package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatRoomResponse;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import java.util.Optional;

public interface ChatService {

    List<ChatRoomResponse> getChatsByMember(String token);

    PrivateChatResponse initiatePrivateChat(String targetProfileId, String content, String token);

    void sendMessage(String chatRoomId, String content, String token);

    void markMessageAsReadForPrivateChat(String messageId, String token);
}
