package com.jygoh.anytime.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
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
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Controller
public class ChatMessageHandler {

    private final ChatService chatService;

    public ChatMessageHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("{chatSessionId}")
    @SendTo("/api/sub/{chatSessionId}")
    public ChatMessageDto sendMessage(@DestinationVariable("chatSessionId") Long chatSessionId,
        @Payload String content, Message<?> message) {
        // STOMP 헤더에서 세션 속성을 가져옵니다.
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        log.info(token);
        // ChatMessageDto를 반환하도록 수정
        return chatService.sendMessage(chatSessionId, token, content);
    }

    @MessageMapping("/accept/{chatRequestId}")
    @SendTo("/topic/chat")
    public ResponseEntity<ChatSessionDto> acceptChatRequest(@PathVariable Long chatRequestId,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        ChatSessionDto chatSessionDto = chatService.acceptChatRequest(chatRequestId, token);
        return ResponseEntity.ok(chatSessionDto);
    }

    @MessageMapping("/reject/{chatRequestId}")
    @SendTo("/topic/chat")
    public ResponseEntity<Void> rejectChatRequest(@PathVariable Long chatRequestId,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        chatService.rejectChatRequest(chatRequestId, token);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/read/{chatMessageId}")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long chatMessageId,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        chatService.markMessageAsRead(chatMessageId, token);
        return ResponseEntity.noContent().build();
    }
}
