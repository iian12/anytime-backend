package com.jygoh.anytime.domain.chat.controller;

import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class MessageController {

    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/private/{chatSessionId}")
    @SendTo("/api/sub/private/{chatSessionId}")
    public ChatMessageDto sendMessage(@DestinationVariable("chatSessionId") String chatSessionId,
        @Payload String content, Message<?> message) {
        // STOMP 헤더에서 세션 속성을 가져옵니다.
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        log.info(token);
        // ChatMessageDto를 반환하도록 수정
        return chatService.sendMessage(chatSessionId, token, content);
    }

    @MessageMapping("/accept/{chatRequestId}")
    @SendTo("/api/sub/{chatRequestId}")
    public ChatSessionDto acceptChatRequest(@PathVariable String chatRequestId,
        Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        return chatService.acceptChatRequest(chatRequestId, token);
    }

    @MessageMapping("/reject/{chatRequestId}")
    @SendTo("/api/sub/{chatRequestId}")
    public void rejectChatRequest(@PathVariable String chatRequestId,
        Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        chatService.rejectChatRequest(chatRequestId, token);
    }

    @MessageMapping("/read/private/{chatMessageId}")
    public void markMessageAsRead(@PathVariable String chatMessageId,
        Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        chatService.markMessageAsRead(chatMessageId, token);
    }
}
