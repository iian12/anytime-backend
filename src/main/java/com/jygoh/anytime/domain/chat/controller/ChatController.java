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
    public ResponseEntity<ChatSessionDto> initiateChat(@RequestBody ChatMessageRequest requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);

        ChatSessionDto chatSession = chatService.initiateChat(requestDto, token); // "Bearer " 제거

        if (chatSession != null) {
            return ResponseEntity.ok(chatSession); // ChatSessionDto가 있는 경우 반환
        } else {
            // 요청을 보낸 경우에는 204 No Content 또는 다른 적절한 응답을 반환할 수 있습니다.
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 요청을 보냈지만 세션이 없을 경우
        }

    }

}
