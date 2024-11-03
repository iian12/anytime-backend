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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private Member target;

    @Column(nullable = false)
    private String messageContent;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 요청 상태 추가

    @Builder
    public ChatRequest(Member requester, Member target, String messageContent) {
        this.requester = requester;
        this.target = target;
        this.messageContent = messageContent;
        this.status = RequestStatus.PENDING; // 기본 상태: 대기 중
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED; // 수락 상태로 변경
    }

    public void reject() {
        this.status = RequestStatus.REJECTED; // 거절 상태로 변경
    }

    public enum RequestStatus {
        PENDING, // 대기 중
        ACCEPTED, // 수락됨
        REJECTED // 거절됨
    }
}
