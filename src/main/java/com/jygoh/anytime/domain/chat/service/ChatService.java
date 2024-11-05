package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatRoomResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatMessageRes;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import java.util.List;

public interface ChatService {

    List<ChatRoomResponse> getChatsByMember(String token);

    PrivateChatResponse initiatePrivateChat(String targetProfileId, String content, String token);

    void sendMessage(String chatRoomId, String content, String token);

    void markMessageAsReadForPrivateChat(String messageId, String token);

    List<PrivateChatMessageRes> getChatHistory(String chatRoomId, String token)
        throws IllegalAccessException;
}
