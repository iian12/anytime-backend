package com.jygoh.anytime.domain.follow.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne
    @JoinColumn(name = "followee_id")
    private Member followee;


    @Builder
    public Follow(Member follower, Member followee) {
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Follow follow)) return false;
        return follower.equals(follow.follower) && followee.equals(follow.followee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, followee);
    }
}
