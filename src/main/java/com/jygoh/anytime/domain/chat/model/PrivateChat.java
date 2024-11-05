package com.jygoh.anytime.domain.chat.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PRIVATE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChat extends Chat {

    @ManyToOne
    @JoinColumn(name = "member1_id", nullable = false)
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2_id", nullable = false)
    private Member member2;

    @OneToMany(mappedBy = "privateChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrivateChatMessage> messages = new ArrayList<>();

    @Builder
    public PrivateChat(Member member1, Member member2) {
        super();
        this.member1 = member1;
        this.member2 = member2;
    }

    public void addMessage(PrivateChatMessage privateChatMessage) {
        messages.add(privateChatMessage);
    }
}
