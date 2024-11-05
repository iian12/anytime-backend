package com.jygoh.anytime.domain.member.repository;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.model.MemberFcmToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFcmTokenRepository extends JpaRepository<MemberFcmToken, Long> {

    Optional<MemberFcmToken> findByMember(Member member);
}
