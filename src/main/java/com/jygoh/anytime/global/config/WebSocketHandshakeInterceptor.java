package com.jygoh.anytime.global.config;

import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 클라이언트 요청에서 Authorization 헤더 추출
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            String token = authHeader.replace("Bearer ", "");  // "Bearer "를 제외한 토큰 값 추출

            // JWT 토큰을 검증하고, 유효한 경우 userId를 반환
            Long userId = TokenUtils.getMemberIdFromToken(token);
            // 인증 성공 시, WebSocket 연결을 위한 userId를 attributes에 저장
            attributes.put("userId", userId);
        } else {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);  // Authorization 헤더가 없으면 401 반환
            return false;  // 인증 실패
        }

        return true;  // 인증 성공, WebSocket 연결을 계속 진행
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {
    }
}
