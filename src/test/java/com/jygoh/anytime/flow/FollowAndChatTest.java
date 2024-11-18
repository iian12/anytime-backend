package com.jygoh.anytime.flow;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jygoh.anytime.domain.chat.controller.MessageController;
import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.repository.GroupChatRepository;
import com.jygoh.anytime.domain.chat.service.ChatService;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class FollowAndChatTest {

    private static final Logger log = LoggerFactory.getLogger(FollowAndChatTest.class);
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
        messageFuture = new CompletableFuture<>();

        token = userCookie1.getValue();
    }

    @Test
    @Transactional
    public void testToggleAndFollowAndInitiateChat() throws Exception {
        // Follow user2 from user1 and vice versa
        mvc.perform(post("/api/v1/follow/toggle").cookie(userCookie1)
                .contentType(MediaType.APPLICATION_JSON).content("{\"profileId\": \"user2Profile\"}"))
            .andExpect(status().isOk());
        mvc.perform(post("/api/v1/follow/toggle").cookie(userCookie2)
                .contentType(MediaType.APPLICATION_JSON).content("{\"profileId\": \"user1Profile\"}"))
            .andExpect(status().isOk());

        // Initiate private chat between user1 and user2
        String chatRequestBody = "{ \"targetProfileId\": \"user2Profile\", \"messageContent\": \"Hello, User2!\" }";
        MvcResult result = mvc.perform(
                post("/api/v1/chat/private").cookie(userCookie1)  // Send token via cookie
                    .contentType(MediaType.APPLICATION_JSON).content(chatRequestBody))
            .andExpect(status().isOk()).andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        PrivateChatResponse responseDto = objectMapper.readValue(responseContent, PrivateChatResponse.class);
        chatId = responseDto.getChatId();
        log.info(chatId);

        // Simulate sending and receiving a message via WebSocket
        String content = "{ \"content\": \"Hello, World\" }";

        // Setup WebSocket client to listen for incoming messages
        BlockingQueue<ChatMessageDto> blockingQueue = new LinkedBlockingQueue<>();
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + token);
        log.info(String.valueOf(headers));
        StompSession session = null;
        try {
            session = client.connect("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Connected to WebSocket server");

                    // Send message through WebSocket
                    session.send("/app/private/" + chatId, content);
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("Transport Error: " + exception.getMessage());
                }
            }, headers).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Error connecting to WebSocket: " + e.getMessage());
        }

        assertThat(session).isNotNull();

        // Listen for response from WebSocket
        StompFrameHandler handler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object payload) {
                ChatMessageDto receivedMessage = (ChatMessageDto) payload;
                blockingQueue.offer(receivedMessage);
            }
        };

        session.subscribe("/user/" + user2.getId() + "/queue/messages", handler);

        // Wait and assert the message
        ChatMessageDto receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.getContent()).isEqualTo(content);
    }
}
