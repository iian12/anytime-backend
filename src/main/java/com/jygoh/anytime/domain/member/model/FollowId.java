package com.jygoh.anytime.domain.member.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class FollowId implements Serializable {

    private Long followerId;
    private Long followeeId;

    public FollowId(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowId followId)) return false;
        return Objects.equals(followerId, followId.followerId) && Objects.equals(followeeId, followId.followeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followeeId);
    }
}
