package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import java.util.Optional;

public interface ChatService {

    Optional<ChatSessionDto> initiateChat(ChatMessageRequest request, String token);

    ChatSessionDto createChatSession(Member requester, Member target, String messageContent);

    ChatSessionDto acceptChatRequest(String chatRequestId, String token);

    void rejectChatRequest(String chatRequestId, String token);

    ChatMessageDto sendMessage(String chatSessionId, String token, String messageContent);

    void markMessageAsRead(String chatMessageId, String token);

    List<ChatSessionDto> getChatSessions(String token);

    void deleteMessage(String messageId, String token);

    List<ChatMessageDto> getMessages(String chatSessionId, String token);
}
