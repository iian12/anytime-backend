package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    @Builder
    public MemberFcmToken(Member member) {
        this.member = member;
    }

    public void addToken(String newToken) {
        boolean tokenExists = fcmTokens
            .stream()
            .anyMatch(token -> token.getToken().equals(newToken));
        if (!tokenExists) {
            this.fcmTokens.add(new FcmToken(this.member, newToken));
        }
    }

    public void removeToken(String tokenToRemove) {
        this.fcmTokens
            .removeIf(token -> token.getToken().equals(tokenToRemove));
    }

    public void updateTokenLastUsedAt(String tokenToUpdate) {
        fcmTokens
            .stream()
            .filter(token -> token.getToken().equals(tokenToUpdate));
    }
}
