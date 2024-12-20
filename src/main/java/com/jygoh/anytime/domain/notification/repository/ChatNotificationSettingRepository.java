package com.jygoh.anytime.domain.notification.repository;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.notification.model.ChatNotificationSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatNotificationSettingRepository extends JpaRepository<ChatNotificationSetting, Long> {

    Optional<ChatNotificationSetting> findByMemberAndChatId(Member member, Long chatId);
}
