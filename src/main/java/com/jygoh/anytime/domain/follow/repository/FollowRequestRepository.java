package com.jygoh.anytime.domain.follow.repository;

import com.jygoh.anytime.domain.follow.model.FollowRequest;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    Optional<FollowRequest> findByRequesterAndTarget(Member requester, Member target);

    void deleteByRequesterAndTarget(Member requester, Member target);
}
