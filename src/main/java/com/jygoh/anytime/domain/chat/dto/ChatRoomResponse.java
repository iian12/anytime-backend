package com.jygoh.anytime.domain.chat.dto;

import com.jygoh.anytime.domain.chat.model.GroupChat;
import com.jygoh.anytime.domain.chat.model.GroupChatMessage;
import com.jygoh.anytime.domain.chat.model.MessageReadStatus;
import com.jygoh.anytime.domain.chat.model.PrivateChat;
import com.jygoh.anytime.domain.chat.model.PrivateChatMessage;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.notification.model.ChatNotificationSetting;
import com.jygoh.anytime.domain.notification.repository.ChatNotificationSettingRepository;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomResponse {

    private static final EncodeDecode encodeDecode = new EncodeDecode();

    private String id;  // 채팅방 ID
    private String title; // 채팅방 제목
    private List<String> memberNicknames;  // 멤버 프로필 ID 리스트
    private boolean notificationsEnabled;  // 알림 설정 여부
    private boolean hasUnreadMessages;  // 읽지 않은 메시지 여부 (요청한 사용자가 읽지 않은 메시지)
    private String chatType;
    private String lastMessageContent;  // 마지막 메시지 내용
    private boolean isLastMessageRead;
    private int unreadMessageCount;

    @Builder
    public ChatRoomResponse(String id, String title, List<String> memberNicknames,
        boolean notificationsEnabled, boolean hasUnreadMessages,
        String chatType, String lastMessageContent, boolean isLastMessageRead, int unreadMessageCount) {
        this.id = id;
        this.title = title;
        this.memberNicknames = memberNicknames;
        this.notificationsEnabled = notificationsEnabled;
        this.hasUnreadMessages = hasUnreadMessages;
        this.chatType = chatType;
        this.lastMessageContent = lastMessageContent;
        this.isLastMessageRead = isLastMessageRead;
        this.unreadMessageCount = unreadMessageCount;
    }

    public static List<ChatRoomResponse> fromChats(List<GroupChat> groupChats,
        List<PrivateChat> privateChats, Member member,
        ChatNotificationSettingRepository notificationSettingsRepository) {

        // 그룹 채팅 처리
        List<ChatRoomResponse> groupChatResponses = groupChats.stream()
            .map(chat -> {
                // 알림 설정 여부 체크
                ChatNotificationSetting settings = notificationSettingsRepository.findByMemberAndChatId(member, chat.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Info"));
                boolean notificationsEnabled = settings != null && settings.isNotificationsEnabled();

                // 요청한 사용자가 읽지 않은 메시지가 있는지 체크
                boolean hasUnreadMessages = chat.getMessages().stream()
                    .anyMatch(msg -> msg.getGroupMessageReadStatuses().stream()
                        .noneMatch(readStatus -> readStatus.getGroupChatMember().getMember().equals(member)));

                // 마지막 메시지 내용 및 읽음 여부
                GroupChatMessage lastMessage = chat.getMessages().stream()
                    .max(Comparator.comparing(GroupChatMessage::getSentAt))
                    .orElse(null);

                String lastMessageContent = (lastMessage != null) ? lastMessage.getContent() : "";
                boolean isLastMessageRead = lastMessage != null && lastMessage.getGroupMessageReadStatuses().stream()
                    .anyMatch(readStatus -> readStatus.getGroupChatMember().getMember().equals(member));

                long unreadMessageCount = chat.getMessages().stream()
                    .filter(msg -> msg.getGroupMessageReadStatuses().stream()
                        .noneMatch(readStatus -> readStatus.getGroupChatMember().getMember().equals(member)))
                    .count();
                return ChatRoomResponse.builder()
                    .id(encodeDecode.encode(chat.getId()))
                    .title(String.valueOf(chat.getTitle()))
                    .memberNicknames(chat.getMembers().stream()
                        .map(memberChat -> memberChat.getMember().getNickname())
                        .collect(Collectors.toList()))
                    .hasUnreadMessages(hasUnreadMessages)
                    .notificationsEnabled(notificationsEnabled)
                    .chatType("GROUP")  // 채팅방 타입
                    .lastMessageContent(lastMessageContent)
                    .isLastMessageRead(isLastMessageRead)
                    .unreadMessageCount((int) unreadMessageCount)
                    .build();
            })
            .toList();

        // 개인 채팅 처리
        List<ChatRoomResponse> privateChatResponses = privateChats.stream()
            .map(chat -> {
                // 알림 설정 여부 체크
                ChatNotificationSetting settings = notificationSettingsRepository.findByMemberAndChatId(member,
                        chat.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Member or Chat"));
                boolean notificationsEnabled = settings != null && settings.isNotificationsEnabled();

                // 요청한 사용자가 읽지 않은 메시지가 있는지 체크
                boolean hasUnreadMessages = chat.getMessages().stream()
                    .anyMatch(msg -> msg.getReadStatus() == MessageReadStatus.UNREAD && !msg.getSender().equals(member));

                // 마지막 메시지 내용 및 읽음 여부
                PrivateChatMessage lastMessage = chat.getMessages().stream()
                    .max(Comparator.comparing(PrivateChatMessage::getSendAt))
                    .orElse(null);

                String lastMessageContent = (lastMessage != null) ? lastMessage.getContent() : "";
                boolean isLastMessageRead = lastMessage != null && lastMessage.getReadStatus() == MessageReadStatus.READ;
                long unreadMessageCount = chat.getMessages().stream()
                    .filter(msg -> msg.getReadStatus() == MessageReadStatus.UNREAD && !msg.getSender().equals(member))
                    .count();

                return ChatRoomResponse.builder()
                    .id(encodeDecode.encode(chat.getId()))
                    .title(chat.getMember1().getId().equals(member.getId())
                        ? chat.getMember2().getNickname()
                        : chat.getMember1().getNickname())
                    .memberNicknames(List.of(
                        chat.getMember1().getId().equals(member.getId())
                            ? chat.getMember2().getProfileId()
                            : chat.getMember1().getProfileId()))
                    .hasUnreadMessages(hasUnreadMessages)
                    .notificationsEnabled(notificationsEnabled)
                    .chatType("PRIVATE")  // 채팅방 타입
                    .lastMessageContent(lastMessageContent)
                    .isLastMessageRead(isLastMessageRead)
                    .unreadMessageCount((int) unreadMessageCount)
                    .build();
            })
            .toList();

        // 그룹 채팅과 개인 채팅 리스트 결합
        List<ChatRoomResponse> combinedResponses = new ArrayList<>(groupChatResponses);
        combinedResponses.addAll(privateChatResponses);

        // 마지막 메시지 날짜 순으로 정렬
        return combinedResponses.stream()
            .sorted(Comparator.comparing(ChatRoomResponse::getLastMessageContent, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }
}
