package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.GroupChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatMemberRepository extends JpaRepository<GroupChatMember, Long> {

}
