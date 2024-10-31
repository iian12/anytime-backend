package com.jygoh.anytime.domain.follow.repository;

import com.jygoh.anytime.domain.follow.model.Follow;
import com.jygoh.anytime.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(Member follower, Member followee);

    void deleteByFollowerAndFollowee(Member follower, Member followee);
}
