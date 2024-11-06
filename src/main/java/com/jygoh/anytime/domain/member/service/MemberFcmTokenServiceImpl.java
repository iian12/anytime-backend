package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.model.FcmToken;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.member.repository.FcmTokenRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberFcmTokenServiceImpl implements MemberFcmTokenService {

    private final MemberRepository memberRepository;
    private final FcmTokenRepository fcmTokenRepository; // FcmTokenRepository를 사용합니다.

    public MemberFcmTokenServiceImpl(FcmTokenRepository fcmTokenRepository, MemberRepository memberRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void addFcmToken(String currentMemberToken, String fcmToken) {
        // 현재 멤버를 가져옵니다. TokenUtils를 사용해 토큰에서 멤버 아이디를 추출하고, 그 아이디로 멤버를 조회합니다.
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(currentMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        // 해당 멤버의 FcmToken을 조회하거나, 없으면 새로 생성합니다.
        Optional<FcmToken> existingToken = fcmTokenRepository.findByMemberAndToken(member, fcmToken);

        if (existingToken.isPresent()) {
            // 이미 존재하면 lastUsedAt을 갱신합니다.
            existingToken.get().updateLastUse();
        } else {
            // 새로운 토큰이라면, FcmToken을 생성하고 해당 Member에 추가합니다.
            FcmToken newFcmToken = FcmToken.builder()
                .member(member)  // FcmToken에 Member 설정
                .token(fcmToken)
                .build();

            // 새로 생성된 FcmToken을 저장합니다.
            fcmTokenRepository.save(newFcmToken);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredFcmTokens() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(60);
        List<FcmToken> allTokens = fcmTokenRepository.findAll();

        for (FcmToken fcmToken : allTokens) {
            if (fcmToken.getLastUsedAt().isBefore(expirationDate)) {
                // 만약 토큰이 만료되었으면 삭제합니다.
                fcmTokenRepository.delete(fcmToken);
            }
        }
    }
}
