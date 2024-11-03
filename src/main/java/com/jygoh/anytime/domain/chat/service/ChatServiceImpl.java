package com.jygoh.anytime.domain.chat.service;

import com.jygoh.anytime.domain.chat.dto.ChatMessageDto;
import com.jygoh.anytime.domain.chat.dto.ChatMessageRequest;
import com.jygoh.anytime.domain.chat.dto.ChatSessionDto;
import com.jygoh.anytime.domain.chat.model.ChatMessage;
import com.jygoh.anytime.domain.chat.model.ChatRequest;
import com.jygoh.anytime.domain.chat.model.MemberChat;
import com.jygoh.anytime.domain.chat.repository.ChatMessageRepository;
import com.jygoh.anytime.domain.chat.repository.ChatRequestRepository;
import com.jygoh.anytime.domain.chat.repository.MemberChatRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final MemberRepository memberRepository;
    private final MemberChatRepository memberChatRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final FollowRepository followRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EncodeDecode encodeDecode;


    public ChatServiceImpl(MemberRepository memberRepository,
        MemberChatRepository memberChatRepository,
        ChatRequestRepository chatRequestRepository,
        FollowRepository followRepository, ChatMessageRepository chatMessageRepository, EncodeDecode encodeDecode) {
        this.memberRepository = memberRepository;
        this.memberChatRepository = memberChatRepository;
        this.chatRequestRepository = chatRequestRepository;
        this.followRepository = followRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.encodeDecode = encodeDecode;
    }

    @Override
    public ChatSessionDto initiateChat(ChatMessageRequest request, String token) {
        Member target = memberRepository.findByProfileId(request.getTargetProfileId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        boolean areMutualFollowers = followRepository.existsByFollowerAndFollowee(requester, target) &&
            followRepository.existsByFollowerAndFollowee(target, requester);

        if (areMutualFollowers) {
            return createChatSession(requester, target, request.getMessageContent());
        } else if (followRepository.existsByFollowerAndFollowee(requester, target)) {
            sendChatRequest(requester, target, request.getMessageContent());
            return null; // 요청을 보냈으므로 세션 DTO는 없음
        } else {
            if (!target.isPrivate()) {
                sendChatRequest(requester, target, request.getMessageContent());
                return null; // 요청을 보냈으므로 세션 DTO는 없음
            }
            throw new RuntimeException("채팅을 시작할 수 없습니다.");
        }
    }

    @Override
    public ChatSessionDto createChatSession(Member requester, Member target, String messageContent) {
        Optional<MemberChat> existingChat = memberChatRepository.findByMember1AndMember2(requester, target);

        if (existingChat.isPresent()) {
            return convertToDTO(existingChat.get());
        }

        MemberChat newMemberChat = MemberChat.builder()
            .member1(requester)
            .member2(target)
            .build();
        memberChatRepository.save(newMemberChat);

        requester.getChatRoomIds().add(newMemberChat.getId());
        target.getChatRoomIds().add(newMemberChat.getId());

        memberRepository.save(requester);
        memberRepository.save(target);

        ChatMessage message = ChatMessage.builder()
            .memberChat(newMemberChat)
            .sender(requester)
            .content(messageContent)
            .build();
        newMemberChat.addMessage(message);

        return convertToDTO(newMemberChat);
    }


    private ChatRequest sendChatRequest(Member requester, Member target, String messageContent) {
        ChatRequest chatRequest = ChatRequest.builder()
            .requester(requester)
            .target(target)
            .messageContent(messageContent)
            .build();

        return chatRequestRepository.save(chatRequest);
    }

    @Override
    public ChatSessionDto acceptChatRequest(String chatRequestId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new RuntimeException("Invalid User"));

        ChatRequest chatRequest = chatRequestRepository.findById(encodeDecode.decode(chatRequestId))
            .orElseThrow(() -> new RuntimeException("채팅 요청을 찾을 수 없습니다."));

        chatRequest.accept();

        MemberChat memberChat = MemberChat.builder()
            .member1(chatRequest.getRequester())
            .member2(chatRequest.getTarget())
            .build();
        memberChatRepository.save(memberChat);

        ChatMessage message = ChatMessage.builder()
            .memberChat(memberChat)
            .sender(chatRequest.getRequester())
            .content(chatRequest.getMessageContent())
            .build();
        message.markAsRead();
        memberChat.addMessage(message);

        chatRequestRepository.delete(chatRequest);

        return convertToDTO(memberChat);
    }

    private ChatSessionDto convertToDTO(MemberChat memberChat) {
        List<ChatMessageDto> messageDTOs = memberChat.getMessages().stream()
            .map(message -> ChatMessageDto.builder()
                .id(encodeDecode.encode(message.getId()))
                .content(message.getContent())
                .timeStamp(message.getTimeStamp())
                .isRead(message.isRead())
                .build())
            .collect(Collectors.toList());

        return ChatSessionDto.builder()
            .id(encodeDecode.encode(memberChat.getId()))
            .member1Nickname(memberChat.getMember1().getNickname())
            .member2Nickname(memberChat.getMember2().getNickname())
            .member2ProfileImageUrl(memberChat.getMember2().getProfileImageUrl())
            .member2ProfileId(memberChat.getMember2().getProfileId())
            .messages(messageDTOs)
            .build();
    }

    @Override
    public void rejectChatRequest(String chatRequestId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new RuntimeException("Invalid User"));

        ChatRequest chatRequest = chatRequestRepository.findById(encodeDecode.decode(chatRequestId))
            .orElseThrow(() -> new RuntimeException("채팅 요청을 찾을 수 없습니다."));

        // 채팅 요청의 요청자가 현재 요청자와 동일한지 확인
        if (!chatRequest.getRequester().equals(requester)) {
            throw new RuntimeException("채팅 요청을 거부할 권한이 없습니다.");
        }

        chatRequest.reject();
        chatRequestRepository.delete(chatRequest);
    }

    @Override
    public ChatMessageDto sendMessage(String chatSessionId, String token, String messageContent) {
        Member sender = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        MemberChat memberChat = memberChatRepository.findById(encodeDecode.decode(chatSessionId))
            .orElseThrow(() -> new RuntimeException("채팅 세션을 찾을 수 없습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
            .memberChat(memberChat)
            .sender(sender)
            .content(messageContent)
            .build();
        memberChat.addMessage(chatMessage);
        chatMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageDto.builder()
            .id(encodeDecode.encode(chatMessage.getId()))
            .content(chatMessage.getContent())
            .timeStamp(chatMessage.getTimeStamp())
            .isRead(chatMessage.isRead())
            .build();
    }

    @Override
    public void markMessageAsRead(String chatMessageId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        ChatMessage chatMessage = chatMessageRepository.findById(encodeDecode.decode(chatMessageId))
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 메시지의 수신자가 현재 요청자와 동일한지 확인
        if (!chatMessage.getMemberChat().getMember2().equals(requester)) {
            throw new RuntimeException("메시지를 읽을 권한이 없습니다.");
        }

        chatMessage.markAsRead();
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatSessionDto> getChatSessions(String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        return member.getChatRoomIds().stream()
            .map(chatRoomId -> memberChatRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅 세션을 찾을 수 없습니다.")))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteMessage(String messageId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        ChatMessage chatMessage = chatMessageRepository.findById(encodeDecode.decode(messageId))
            .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        if (chatMessage.getSender().equals(requester)) {
            chatMessage.setDeletedByRequest();
        } else if (chatMessage.getMemberChat().getMember2().equals(requester)) {
            chatMessage.setDeletedByTarget();
        } else {
            throw new RuntimeException("메시지를 삭제할 권한이 없습니다.");
        }

        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessageDto> getMessages(String chatSessionId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        MemberChat memberChat = memberChatRepository.findById(encodeDecode.decode(chatSessionId))
            .orElseThrow(() -> new RuntimeException("채팅 세션을 찾을 수 없습니다."));

        return memberChat.getMessages().stream()
            .filter(message -> {
                if (message.getSender().equals(requester) && message.isDeletedByRequester()) {
                    return false;
                } else if (message.getMemberChat().getMember2().equals(requester) && message.isDeletedByTarget()) {
                    return false;
                }
                return true;
            })
            .map(message -> ChatMessageDto.builder()
                .id(encodeDecode.encode(message.getId()))
                .content(message.getContent())
                .timeStamp(message.getTimeStamp())
                .isRead(message.isRead())
                .build()).collect(Collectors.toList());
    }
}
