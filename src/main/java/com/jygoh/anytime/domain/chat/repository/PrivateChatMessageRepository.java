package com.jygoh.anytime.domain.chat.repository;

import com.jygoh.anytime.domain.chat.model.PrivateChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, Long> {

    List<PrivateChatMessage> findAllByPrivateChatId(Long decodedChatRoomId);

}
