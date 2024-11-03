package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_chat_id", nullable = false)
    private MemberChat memberChat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    boolean isRead;

    private boolean deletedByRequester;
    private boolean deletedByTarget;


    @Builder
    public ChatMessage(MemberChat memberChat, Member sender, String content) {
        this.memberChat = memberChat;
        this.sender = sender;
        this.content = content;
        this.timeStamp = LocalDateTime.now();
        this.isRead = false;
        this.deletedByRequester = false;
        this.deletedByTarget = false;
    }
    public void markAsRead() {
        this.isRead = true;
    }
    public void setDeletedByRequest() {
        this.deletedByRequester = true;
    }
    public void setDeletedByTarget() {
        this.deletedByTarget = true;
    }
}
