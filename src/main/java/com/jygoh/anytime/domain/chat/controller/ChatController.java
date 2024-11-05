package com.jygoh.anytime.domain.chat.controller;

import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatRoomResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatMessageRes;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/room")
    public ResponseEntity<List<ChatRoomResponse>> getChatsByMember(HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        List<ChatRoomResponse> chatRooms = chatService.getChatsByMember(token);

        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/private")
    public ResponseEntity<PrivateChatResponse> initiatePrivateChat(@RequestBody ChatMessageRequest requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);

        PrivateChatResponse chatResponse = chatService.initiatePrivateChat(requestDto.getTargetProfileId(), requestDto.getMessageContent(), token);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<List<PrivateChatMessageRes>> getChatHistory(@PathVariable String chatRoomId, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        try {
            List<PrivateChatMessageRes> chatHistory = chatService.getChatHistory(chatRoomId, token);
            return ResponseEntity.ok(chatHistory);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
