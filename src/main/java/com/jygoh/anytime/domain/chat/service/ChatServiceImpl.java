package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatRoomResponse;
import com.jygoh.anytime.domain.chat.dto.GroupChatResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatMessageRes;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse;
import com.jygoh.anytime.domain.chat.dto.PrivateChatResponse.ChatStatus;
import com.jygoh.anytime.domain.chat.dto.ReadReceipt;
import com.jygoh.anytime.domain.chat.model.ChatRequest;
import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.model.GroupChatMember;
import com.jygoh.anytime.domain.chat.model.MessageReadStatus;
import com.jygoh.anytime.domain.chat.model.PrivateChat;
import com.jygoh.anytime.domain.chat.model.PrivateChatMessage;
import com.jygoh.anytime.domain.chat.repository.ChatRequestRepository;
import com.jygoh.anytime.domain.chat.repository.GroupChatMemberRepository;
import com.jygoh.anytime.domain.chat.repository.GroupChatRepository;
import com.jygoh.anytime.domain.chat.repository.PrivateChatMessageRepository;
import com.jygoh.anytime.domain.chat.repository.PrivateChatRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.notification.repository.ChatNotificationSettingRepository;
import com.jygoh.anytime.global.security.jwt.utils.BlockValidator;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
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
    private final ChatNotificationSettingRepository notificationSettingRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;

    public ChatServiceImpl(MemberRepository memberRepository, FollowRepository followRepository,
        PrivateChatRepository privateChatRepository, ChatRequestRepository chatRequestRepository,
        GroupChatRepository groupChatRepository, BlockValidator blockValidator,
        EncodeDecode encodeDecode, PrivateChatMessageRepository privateChatMessageRepository,
        SimpMessagingTemplate messagingTemplate,
        ChatNotificationSettingRepository notificationSettingRepository,
        GroupChatMemberRepository groupChatMemberRepository) {
        this.memberRepository = memberRepository;
        this.followRepository = followRepository;
        this.privateChatRepository = privateChatRepository;
        this.chatRequestRepository = chatRequestRepository;
        this.groupChatRepository = groupChatRepository;
        this.blockValidator = blockValidator;
        this.encodeDecode = encodeDecode;
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationSettingRepository = notificationSettingRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
    }

    @Override
    public List<ChatRoomResponse> getChatsByMember(String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        List<GroupChat> groupChats = groupChatRepository.findAllByMemberId(
            TokenUtils.getMemberIdFromToken(token));
        List<PrivateChat> privateChats = privateChatRepository.findAllByMemberId(
            TokenUtils.getMemberIdFromToken(token));
        return
            ChatRoomResponse
                .fromChats(groupChats, privateChats, member, notificationSettingRepository);
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

    @Override
    public GroupChatResponse initiateGroupChat(List<String> targetProfileIds, String title,
        String token) {

        Member requesterMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        // 그룹 채팅에 참여할 사용자들을 찾기
        List<Member> targetMembers = memberRepository.findByProfileIdIn(targetProfileIds);
        if (targetMembers.isEmpty() || targetMembers.size() < 2) {
            throw new IllegalArgumentException("인원을 2명 이상 선택하세요.");
        }
        if (title == null) {
            throw new IllegalArgumentException("그룹 이름을 지정해주세요.");
        }

        // 요청자가 상호 팔로우 관계에 있는지 확인
        for (Member targetMember : targetMembers) {
            // 요청자와 목표 사용자가 서로 팔로우하고 있는지 확인
            boolean isRequesterFollowing = followRepository.existsByFollowerAndFollowee(requesterMember, targetMember);
            boolean isTargetFollowing = followRepository.existsByFollowerAndFollowee(targetMember, requesterMember);

            // 상호 팔로우 관계가 아닌 경우 예외 처리
            if (!(isRequesterFollowing && isTargetFollowing)) {
                throw new IllegalArgumentException("팔로우 관계가 없습니다.");
            }

            // 블록된 사용자가 있는지 확인
            blockValidator.validateNotBlocked(requesterMember, targetMember); // 요청자가 차단한 사용자가 있는지 확인
        }

        GroupChat groupChat = GroupChat.builder()
            .title(title)
            .build();

        // 그룹 채팅 저장
        groupChatRepository.save(groupChat);

        log.info(String.valueOf(groupChat.getId()));
        GroupChatMember requesterGroupChatMember = GroupChatMember.builder()
            .groupChat(groupChat)
            .member(requesterMember)
            .build();

        groupChatMemberRepository.save(requesterGroupChatMember);

        // 그룹 채팅에 참여할 사용자들을 추가
        for (Member targetMember : targetMembers) {
            // 각 멤버를 GroupChatMember에 추가
            GroupChatMember groupChatMember = GroupChatMember.builder()
                .groupChat(groupChat)
                .member(targetMember)
                .build();

            groupChatMemberRepository.save(groupChatMember); // GroupChatMember 저장
        }

        // 그룹 채팅 생성에 대한 응답을 반환
        return GroupChatResponse.builder()
            .chatId(encodeDecode.encode(groupChat.getId()))
            .memberProfileIds(targetProfileIds) // 필요에 따라 채팅 상태 설정
            .build();
    }

    private PrivateChatResponse startChatSession(Member currentMember, String content,
        Member targetMember) {
        PrivateChat privateChat = createPrivateChat(currentMember, targetMember, content);

        return PrivateChatResponse.builder().chatId(encodeDecode.encode(privateChat.getId()))
            .status(ChatStatus.STARTED)
            .build();
    }

    private PrivateChatResponse startChatSessionWithRequest(Member currentMember, String content,
        Member targetMember) {
        ChatRequest chatRequest = chatRequestRepository.findByRequesterAndTarget(currentMember, targetMember)
            .orElseGet(() -> {
                // 기존 요청이 없을 경우 새로운 요청을 생성하여 저장
                ChatRequest newRequest = ChatRequest.builder()
                    .requester(currentMember)
                    .target(targetMember)
                    .build();
                return chatRequestRepository.save(newRequest);
            });

        PrivateChat privateChat = createPrivateChat(currentMember, targetMember, content);

        return PrivateChatResponse.builder().requestId(encodeDecode.encode(chatRequest.getId()))
            .chatId(encodeDecode.encode(privateChat.getId())).status(ChatStatus.PENDING_REQUEST)
            .build();
    }

    private PrivateChat createPrivateChat(
        Member currentMember, Member targetMember, String content) {
        PrivateChat privateChat = PrivateChat.builder()
            .member1(currentMember)
            .member2(targetMember)
            .build();
        privateChatRepository.save(privateChat);
        PrivateChatMessage message = PrivateChatMessage.builder()
            .privateChat(privateChat)
            .sender(currentMember)
            .content(content)
            .build();
        privateChatMessageRepository.save(message);
        privateChat.addMessage(message);
        log.info(String.valueOf(privateChat.getId()));
        return privateChat;
    }

    @Override
    public void sendMessage(String chatRoomId, String content, String token) {

        PrivateChat chat = privateChatRepository.findById(encodeDecode.decode(chatRoomId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Chat Room"));

        Member sender = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        PrivateChatMessage newMessage = PrivateChatMessage.builder()
            .privateChat(chat)
            .sender(sender)
            .content(content)
            .build();
        privateChatMessageRepository.save(newMessage);

        chat.addMessage(newMessage);

        messagingTemplate.convertAndSend(
            "/api/sub/" + chat.getId(),
            newMessage
            );
    }

    @Override
    public void markMessageAsReadForPrivateChat(String messageId, String token) {

        PrivateChatMessage message =
            privateChatMessageRepository.findById(encodeDecode.decode(messageId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Message"));

        if (message.getReadStatus() != MessageReadStatus.READ) {
            message.markAsRead();
            privateChatMessageRepository.save(message);

            messagingTemplate.convertAndSend(
                "/api/sub/chatRoom/" + message.getPrivateChat().getId(),
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

        if (!chat.getMember1().getId().equals(memberId)
            && !chat.getMember2().getId().equals(memberId)) {
            throw new
                IllegalAccessException("Access Denied: You are not a member of this chat room");
        }

        List<PrivateChatMessage> messages =
            privateChatMessageRepository.findAllByPrivateChatId(decodedChatRoomId);

        return messages.stream()
            .map(message -> PrivateChatMessageRes.from(message, memberId))
            .collect(Collectors.toList());
    }
}
