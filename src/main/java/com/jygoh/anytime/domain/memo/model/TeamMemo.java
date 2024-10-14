package com.jygoh.anytime.domain.memo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Long memberId;

    private Long teamId;

    @Builder
    public TeamMemo(String title, String content, Long memberId, Long teamId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.teamId = teamId;
    }

    public void updateTeamMemo(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
