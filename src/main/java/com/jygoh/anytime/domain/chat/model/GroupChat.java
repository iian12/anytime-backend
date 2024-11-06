package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupChat extends Chat {

    private String title;

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupChatMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupChatMessage> messages = new ArrayList<>();

    @Builder
    public GroupChat(String title) {
        this.title = title;
    }

    public void addMember(GroupChatMember member) {
        members.add(member);
    }

    public void addMessage(GroupChatMessage message) {
        messages.add(message);
    }

    public void markMessageAsRead(GroupChatMessage message, Member member) {
        GroupChatMember groupChatMember = members.stream()
            .filter(m -> m.getMember().equals(member))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 채팅에 존재하지 않습니다."));
        groupChatMember.markMessageAsRead(message);
    }
}
