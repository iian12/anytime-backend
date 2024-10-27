package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String color;

    public MemberRole(Long memberId, Long teamId, String role, String color) {
        this.memberId = memberId;
        this.teamId = teamId;
        this.role = role;
        this.color = color;
    }

    public void addColor(String color) {
        this.color = color;
    }
}
