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
public class GroupMessageReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private GroupChatMember groupChatMember;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private GroupChatMessage groupChatMessage;

    @Builder
    public GroupMessageReadStatus(GroupChatMember groupChatMember, GroupChatMessage groupChatMessage) {
        this.groupChatMember = groupChatMember;
        this.groupChatMessage = groupChatMessage;
    }
}
