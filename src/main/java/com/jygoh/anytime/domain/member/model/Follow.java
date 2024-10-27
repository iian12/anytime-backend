package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne
    @MapsId("followeeId")
    @JoinColumn(name = "followee_id")
    private Member followee;


    @Builder
    public Follow(Member follower, Member followee) {
        this.follower = follower;
        this.followee = followee;
        this.id = new FollowId(follower.getId(), followee.getId());
    }

}
