package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.PrivateChat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {
    @Query("SELECT pc FROM PrivateChat pc WHERE pc.member1.id = :memberId OR pc.member2.id = :memberId")
    List<PrivateChat> findAllByMemberId(@Param("memberId") Long memberId);

}
