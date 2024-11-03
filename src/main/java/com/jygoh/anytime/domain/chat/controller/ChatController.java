package com.jygoh.anytime.domain.chat.controller;

import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateChat(@RequestBody ChatMessageRequest requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        Optional<ChatSessionDto> chatSession = chatService.initiateChat(requestDto, token); // "Bearer " 제거
        // ChatSessionId 반환
        // 요청 보냈을 때의 경우
        return chatSession.map(chatSessionDto -> ResponseEntity.ok(chatSessionDto.getId()))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).build());
    }

}
