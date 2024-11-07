package com.jygoh.anytime.flow;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jygoh.anytime.domain.chat.controller.MessageController;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.repository.GroupChatRepository;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class FollowAndChatTest {

    @Mock
    private Member user1;
    @Mock
    private Member user2;
    @Autowired
    private MemberRepository memberRepository;

    @InjectMocks
    private MessageController messageController;

    @Autowired
    private MockMvc mvc;

    private WebSocketStompClient client;
    private CompletableFuture<String> messageFuture;

    private MockCookie userCookie1;
    private MockCookie userCookie2;

    @Mock
    private ChatService chatService;

    @Autowired
    private EncodeDecode encodeDecode;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String chatId;
    @Autowired
    private GroupChatRepository groupChatRepository;

    String token;

    @BeforeEach
    public void setUp() {
        user1 = Member.builder()
            .email("user1@example.com")
            .profileId("user1Profile")
            .password("password1")
            .nickname("User1")
            .isSignUpComplete(true)
            .isPrivate(false)
            .followingCount(0)
            .followerCount(0)
            .profileImageUrl("https://example.com/user1.jpg")
            .postCount(0)
            .providerId("providerId1")
            .subjectId("subjectId1")
            .build();
        user2 = Member.builder()
            .email("user2@example.com")
            .profileId("user2Profile")
            .password("password2")
            .nickname("User2")
            .isSignUpComplete(true)
            .isPrivate(false)
            .followingCount(0)
            .followerCount(0)
            .profileImageUrl("https://example.com/user2.jpg")
            .postCount(0)
            .providerId("providerId2")
            .subjectId("subjectId2")
            .build();
        memberRepository.save(user1);
        memberRepository.save(user2);
        userCookie1 = new MockCookie("access_token",
            jwtTokenProvider.createAccessToken(user1.getId()));
        userCookie2 = new MockCookie("access_token",
            jwtTokenProvider.createAccessToken(user2.getId()));
        client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());
        messageFuture = new CompletableFuture<>();

        token = userCookie1.getValue();
    }

    @Test
    @Transactional
    public void testToggleAndFollowAndInitiateChat() throws Exception {
        mvc.perform(post("/api/v1/follow/toggle").cookie(userCookie1)
                .contentType(MediaType.APPLICATION_JSON).content("{\"profileId\": \"user2Profile\"}"))
            .andExpect(status().isOk());
        mvc.perform(post("/api/v1/follow/toggle").cookie(userCookie2)
                .contentType(MediaType.APPLICATION_JSON).content("{\"profileId\": \"user1Profile\"}"))
            .andExpect(status().isOk());
        String chatRequestBody = "{ \"targetProfileId\": \"user2Profile\", \"messageContent\": \"Hello, User2!\" }";
        MvcResult result = mvc.perform(
                post("/api/v1/chat/private").cookie(userCookie1)  // 토큰을 쿠키로 전달
                    .contentType(MediaType.APPLICATION_JSON).content(chatRequestBody))
            .andExpect(status().isOk()).andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PrivateChatResponse responseDto = objectMapper.readValue(responseContent, PrivateChatResponse.class);
        chatId = responseDto.getChatId();
        System.out.println(chatId);
        GroupChat chat = GroupChat.builder()
            .title("title")
            .build();

        groupChatRepository.save(chat);
        System.out.println(chat.getId());

        String content = "Hello, World";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        if (accessor.getSessionAttributes() == null) {
            accessor.setSessionAttributes(new HashMap<>()); // 세션 속성 초기화
        }
        accessor.getSessionAttributes().put("Authorization", "Bearer " + token);
        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        messageController.sendMessage(chatId, content, message);

        verify(chatService, times(1)).sendMessage(chatId, content, "Bearer " + token);
    }
}
