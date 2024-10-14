package com.jygoh.anytime.domain.team.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamLeaderId;

    @Column(nullable = false)
    private String teamName;

    private String description;

    @ElementCollection
    private List<Long> memberIds = new ArrayList<>();

    @Builder
    public Team(Long teamLeaderId, String teamName, String description) {
        this.teamLeaderId = teamLeaderId;
        this.teamName = teamName;
        this.description = description;
    }

    public void updateTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void addMemberId(Long memberId) {
        this.memberIds.add(memberId);
    }

    public void removeMemberId(Long memberId) {
        this.memberIds.remove(memberId);
    }

    public void delegateTeamLeader(Long newTeamLeaderId) {
        this.teamLeaderId = newTeamLeaderId;
    }
}
