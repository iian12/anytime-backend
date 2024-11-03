package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
