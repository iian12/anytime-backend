package com.jygoh.anytime.domain.member.repository;

import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByProfileId(String profileId);

    Optional<Member> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByProfileId(String profileId);

    List<Member> findByProfileIdIn(List<String> targetProfileIds);
}
