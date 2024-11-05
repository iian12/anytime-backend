package com.jygoh.anytime.domain.member.block;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blcker_id", nullable = false)
    private Member blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @Builder
    public Block(Member blocker, Member blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
