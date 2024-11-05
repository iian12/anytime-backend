package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ElementCollection
    @CollectionTable(name = "member_fcm_tokens", joinColumns = @JoinColumn(name = "member_fcm_token_id"))
    private List<String> fcmTokens = new ArrayList<>();

    @Builder
    public MemberFcmToken(Member member, List<String> fcmToken) {
        this.member = member;
        this.fcmTokens = fcmToken;
    }

    public void addToken(String newToken) {
        if (!this.fcmTokens.contains(newToken)) {
            this.fcmTokens.add(newToken);
        }
    }

    public void removeToken(String tokenToRemove) {
        this.fcmTokens.remove(tokenToRemove);
    }
}
