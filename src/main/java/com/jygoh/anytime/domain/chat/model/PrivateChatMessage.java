package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private PrivateChat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageReadStatus readStatus;
    private LocalDateTime sendAt;
    private LocalDateTime readAt;

    @Builder
    public PrivateChatMessage(PrivateChat chat, Member sender, String content) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.readStatus = MessageReadStatus.UNREAD;
        this.sendAt = LocalDateTime.now();
        this.readAt = null;
    }

    public void markAsRead() {
        this.readStatus = MessageReadStatus.READ;
        this.readAt = LocalDateTime.now();
    }
}
