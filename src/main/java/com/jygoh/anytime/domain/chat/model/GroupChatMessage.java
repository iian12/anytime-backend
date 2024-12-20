package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_chat_id", nullable = false)
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column(nullable = false)
    private String content;

    private LocalDateTime sentAt;

    @OneToMany(mappedBy = "groupChatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMessageReadStatus> groupMessageReadStatuses = new ArrayList<>();

    @Builder
    public GroupChatMessage(GroupChat groupChat, Member sender, String content) {
        this.groupChat = groupChat;
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDateTime.now();

        groupChat.markMessageAsRead(this, sender);
    }
}
