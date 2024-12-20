package com.jygoh.anytime.domain.notification.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ChatNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean notificationsEnabled;

    private Long chatId;

    @Builder
    public ChatNotificationSetting(Member member, Long chatId) {
        this.member = member;
        this.chatId = chatId;
        this.notificationsEnabled = true;
    }

    public void toggleNotifications() {
        this.notificationsEnabled = !this.notificationsEnabled;
    }
}
