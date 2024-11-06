package com.jygoh.anytime.global.handler;

import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public StompHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더에서 토큰 추출
            String token = accessor.getFirstNativeHeader("Authorization");
            log.info(token);
            // 토큰이 유효한지 검증
            if (token == null || !this.jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("Invalid or missing token");
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer "를 제거
            }
            Objects.requireNonNull(accessor.getSessionAttributes()).put("Authorization", token);
            log.info("Session Attributes: {}", accessor.getSessionAttributes());
        }

        return message;
    }
}
