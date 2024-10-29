package com.jygoh.anytime.domain.hashtag.repository;

import com.jygoh.anytime.domain.hashtag.model.Hashtag;
import com.jygoh.anytime.domain.hashtag.model.HashtagFollower;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagFollowerRepository extends JpaRepository<HashtagFollower, Long> {

    boolean existsByMemberAndHashtag(Member member, Hashtag hashtag);

    void deleteByMemberAndHashtag(Member member, Hashtag hashtag);

    List<HashtagFollower> findAllByMember(Member member);
}
