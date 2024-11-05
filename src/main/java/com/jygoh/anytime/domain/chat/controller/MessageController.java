package com.jygoh.anytime.domain.chat.controller;

import com.jygoh.anytime.domain.chat.service.ChatService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    public void sendMessage(@DestinationVariable("chatSessionId") String chatSessionId,
        @Payload String content, Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");

        chatService.sendMessage(chatSessionId, token, content);
    }

    @MessageMapping("/read/private/{chatMessageId}")
    public void markMessageAsRead(@PathVariable String chatMessageId,
        Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("Authorization");
        chatService.markMessageAsReadForPrivateChat(chatMessageId, token);
    }
}
