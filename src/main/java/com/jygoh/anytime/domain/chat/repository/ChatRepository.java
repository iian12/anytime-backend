package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
