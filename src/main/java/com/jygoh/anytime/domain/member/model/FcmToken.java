package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member와 FcmToken 간의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;;

    private String token;

    private LocalDateTime lastUsedAt;

    @Builder
    public FcmToken(Member member, String token) {
        this.member = member;
        this.token = token;
        this.lastUsedAt = LocalDateTime.now();
    }

    public void updateLastUse() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
