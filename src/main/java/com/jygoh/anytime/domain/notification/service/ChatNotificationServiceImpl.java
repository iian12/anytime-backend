package com.jygoh.anytime.domain.notification.service;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.notification.model.ChatNotificationSetting;
import com.jygoh.anytime.domain.notification.repository.ChatNotificationSettingRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatNotificationServiceImpl implements ChatNotificationService {

    private final ChatNotificationSettingRepository notificationSettingRepository;
    private final MemberRepository memberRepository;
    private final EncodeDecode encodeDecode;

    public ChatNotificationServiceImpl(ChatNotificationSettingRepository notificationSettingRepository,
        MemberRepository memberRepository, EncodeDecode encodeDecode) {
        this.notificationSettingRepository = notificationSettingRepository;
        this.memberRepository = memberRepository;
        this.encodeDecode = encodeDecode;
    }

    @Override
    public boolean toggleChatNotification(String chatId, String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        ChatNotificationSetting setting = notificationSettingRepository
            .findByMemberAndChatId(member, encodeDecode.decode(chatId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Setting"));

        setting.toggleNotifications();
        notificationSettingRepository.save(setting);

        return setting.isNotificationsEnabled();
    }
}
