package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.ChatRequest;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    Optional<ChatRequest> findByRequesterAndTarget(Member requester, Member target);
}
