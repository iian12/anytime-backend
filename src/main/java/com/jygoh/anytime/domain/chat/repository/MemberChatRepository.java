package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.MemberChat;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRepository extends JpaRepository<MemberChat, Long> {

    Optional<MemberChat> findByMember1AndMember2(Member requester, Member target);
}
