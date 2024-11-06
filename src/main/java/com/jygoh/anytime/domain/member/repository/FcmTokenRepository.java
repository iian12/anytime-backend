package com.jygoh.anytime.domain.member.repository;

import com.jygoh.anytime.domain.member.model.FcmToken;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberAndToken(Member member, String fcmToken);
}
