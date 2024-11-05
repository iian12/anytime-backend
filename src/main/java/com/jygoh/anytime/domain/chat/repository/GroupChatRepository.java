package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.GroupChat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    @Query("SELECT gc FROM GroupChat gc JOIN gc.members m WHERE m.id = :memberId")
    List<GroupChat> findAllByMemberId(@Param("memberId") Long memberId);
}
