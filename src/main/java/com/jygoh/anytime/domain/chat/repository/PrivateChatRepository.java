package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.PrivateChat;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {
    @Query("SELECT pc FROM PrivateChat pc WHERE pc.member1.id = :memberId OR pc.member2.id = :memberId")
    List<PrivateChat> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT pc FROM PrivateChat pc WHERE (pc.member1.id = :member1Id AND pc.member2.id = :member2Id) " +
        "OR (pc.member1.id = :member2Id AND pc.member2.id = :member1Id)")
    Optional<PrivateChat> findByMembers(@Param("member1Id") Long member1Id, @Param("member2Id") Long member2Id  );
}
