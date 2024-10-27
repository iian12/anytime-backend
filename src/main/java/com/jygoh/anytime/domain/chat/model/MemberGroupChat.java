package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "group_chat_id")
    private GroupChat groupChat;

    private LocalDateTime joinedAt;

    @Builder
    public MemberGroupChat(Member member, GroupChat groupChat, LocalDateTime joinedAt) {
        this.member = member;
        this.groupChat = groupChat;
        this.joinedAt = joinedAt;
    }

}
