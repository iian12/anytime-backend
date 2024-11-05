package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatRoomResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatMessageRes;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse.ChatStatus;
import com.jygoh.anytime.domain.chat.dto.ReadReceipt;
import com.jygoh.anytime.domain.chat.model.ChatRequest;
import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.model.MessageReadStatus;
import com.jygoh.anytime.domain.chat.model.PrivateChat;
import com.jygoh.anytime.domain.chat.model.PrivateChatMessage;
import com.jygoh.anytime.domain.chat.repository.ChatRequestRepository;
import com.jygoh.anytime.domain.chat.repository.GroupChatRepository;
import com.jygoh.anytime.domain.chat.repository.PrivateChatMessageRepository;
import com.jygoh.anytime.domain.chat.repository.PrivateChatRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.BlockValidator;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final PrivateChatRepository privateChatRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final GroupChatRepository groupChatRepository;
    private final BlockValidator blockValidator;
    private final EncodeDecode encodeDecode;
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImpl(MemberRepository memberRepository, FollowRepository followRepository,
        PrivateChatRepository privateChatRepository, ChatRequestRepository chatRequestRepository,
        GroupChatRepository groupChatRepository, BlockValidator blockValidator,
        EncodeDecode encodeDecode, PrivateChatMessageRepository privateChatMessageRepository,
        SimpMessagingTemplate messagingTemplate) {
        this.memberRepository = memberRepository;
        this.followRepository = followRepository;
        this.privateChatRepository = privateChatRepository;
        this.chatRequestRepository = chatRequestRepository;
        this.groupChatRepository = groupChatRepository;
        this.blockValidator = blockValidator;
        this.encodeDecode = encodeDecode;
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<ChatRoomResponse> getChatsByMember(String token) {
        Long memberId = TokenUtils.getMemberIdFromToken(token);
        List<GroupChat> groupChats = groupChatRepository.findAllByMemberId(
            TokenUtils.getMemberIdFromToken(token));
        List<PrivateChat> privateChats = privateChatRepository.findAllByMemberId(
            TokenUtils.getMemberIdFromToken(token));
        return ChatRoomResponse.fromChats(groupChats, privateChats, memberId);
    }

    @Override
    public PrivateChatResponse initiatePrivateChat(String targetProfileId, String content,
        String token) {
        Member requesterMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        Member targetMember = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        blockValidator.validateNotBlocked(requesterMember, targetMember);
        boolean isFollowing = followRepository.existsByFollowerAndFollowee(requesterMember,
            targetMember);
        boolean isFollowed = followRepository.existsByFollowerAndFollowee(targetMember,
            requesterMember);
        boolean isTargetPrivate = targetMember.isPrivate();
        if (isFollowing && isFollowed) {
            return startChatSession(requesterMember, content, targetMember);
        }
        if (!isTargetPrivate) {
            return startChatSessionWithRequest(requesterMember, content, targetMember);
        }
        if (isFollowed) {
            return startChatSession(requesterMember, content, targetMember);
        }
        throw new IllegalArgumentException("비공개 계정의 사용자입니다.");
    }

    private PrivateChatResponse startChatSession(Member currentMember, String content,
        Member targetMember) {
        PrivateChat newChat = PrivateChat.builder().member1(currentMember).member2(targetMember)
            .build();
        privateChatRepository.save(newChat);
        PrivateChatMessage privateChatMessage = PrivateChatMessage.builder()
            .chat(newChat)
            .sender(currentMember)
            .content(content)
            .build();
        privateChatMessageRepository.save(privateChatMessage);
        newChat.addMessage(privateChatMessage);
        return PrivateChatResponse.builder().chatId(encodeDecode.encode(newChat.getId()))
            .build();
    }

    private PrivateChatResponse startChatSessionWithRequest(Member currentMember, String content,
        Member targetMember) {
        ChatRequest newRequest = ChatRequest.builder().requester(currentMember).target(targetMember)
            .build();
        chatRequestRepository.save(newRequest);
        PrivateChat privateChat = PrivateChat.builder().member1(currentMember).member2(targetMember)
            .build();
        privateChatRepository.save(privateChat);
        PrivateChatMessage privateChatMessage = PrivateChatMessage.builder()
            .chat(privateChat)
            .sender(currentMember)
            .content(content)
            .build();
        privateChatMessageRepository.save(privateChatMessage);
        privateChat.addMessage(privateChatMessage);
        return PrivateChatResponse.builder().requestId(encodeDecode.encode(newRequest.getId()))
            .chatId(encodeDecode.encode(privateChat.getId())).status(ChatStatus.PENDING_REQUEST)
            .build();
    }

    @Override
    public void sendMessage(String chatRoomId, String content, String token) {

        PrivateChat chat = privateChatRepository.findById(encodeDecode.decode(chatRoomId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Chat Room"));

        Member sender = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        PrivateChatMessage newMessage = PrivateChatMessage.builder()
            .chat(chat)
            .sender(sender)
            .content(content)
            .build();
        privateChatMessageRepository.save(newMessage);

        chat.addMessage(newMessage);

        messagingTemplate.convertAndSend(
            "/api/sub/chatRoom/" + chat.getId(),
            newMessage
            );
    }

    @Override
    public void markMessageAsReadForPrivateChat(String messageId, String token) {

        PrivateChatMessage message = privateChatMessageRepository.findById(encodeDecode.decode(messageId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Message"));

        if (message.getReadStatus() != MessageReadStatus.READ) {
            message.markAsRead();
            privateChatMessageRepository.save(message);

            messagingTemplate.convertAndSend(
                "/api/sub/chatRoom/" + message.getChat().getId(),
                new ReadReceipt(messageId, true, message.getReadAt())
            );
        }
    }

    @Override
    public List<PrivateChatMessageRes> getChatHistory(String chatRoomId, String token)
        throws IllegalAccessException {
        Long memberId = TokenUtils.getMemberIdFromToken(token);
        Long decodedChatRoomId = encodeDecode.decode(chatRoomId);

        PrivateChat chat = privateChatRepository.findById(decodedChatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Chat Room"));

        if (!chat.getMember1().getId().equals(memberId) && !chat.getMember2().getId().equals(memberId)) {
            throw new IllegalAccessException("Access Denied: You are not a member of this chat room");
        }

        List<PrivateChatMessage> messages = privateChatMessageRepository.findAllByChatId(decodedChatRoomId);

        return messages.stream()
            .map(message -> PrivateChatMessageRes.from(message, memberId))
            .collect(Collectors.toList());
    }
}
