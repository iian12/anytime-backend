package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.ChatRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

}
