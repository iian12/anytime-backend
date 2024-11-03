package com.jygoh.anytime.domain.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatName;

    @OneToMany(mappedBy = "groupChat")
    private List<MemberGroupChat> memberGroupChats = new ArrayList<>();

    @Builder
    public GroupChat(String chatName, List<MemberGroupChat> memberGroupChats) {
        this.chatName = chatName;
        this.memberGroupChats = memberGroupChats != null ? memberGroupChats : new ArrayList<>();
    }

    public void updateChatName(String newChatName) {
        this.chatName = newChatName;
    }

}
