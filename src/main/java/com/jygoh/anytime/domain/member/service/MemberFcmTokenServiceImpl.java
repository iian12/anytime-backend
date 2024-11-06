package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.model.FcmToken;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.model.MemberFcmToken;
import com.jygoh.anytime.domain.member.repository.MemberFcmTokenRepository;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
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
    private final MemberFcmTokenRepository memberFcmTokenRepository;

    public MemberFcmTokenServiceImpl(MemberFcmTokenRepository memberFcmTokenRepository,
        MemberRepository memberRepository) {
        this.memberFcmTokenRepository = memberFcmTokenRepository;
        this.memberRepository = memberRepository;
    }


    @Override
    public void addFcmToken(String currentMemberToken, String fcmToken) {

        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(currentMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        MemberFcmToken memberFcmToken = memberFcmTokenRepository.findByMember(member)
            .orElseGet(() -> {
                MemberFcmToken newToken = new MemberFcmToken(member);
                return memberFcmTokenRepository.save(newToken);
            });
        Optional<FcmToken> existingToken = memberFcmToken.getFcmTokens().stream()
            .filter(token -> token.getToken().equals(fcmToken))
            .findFirst();

        if (existingToken.isPresent()) {
            existingToken.get().updateLastUse();
        } else {
            FcmToken newFcmToken = FcmToken.builder()
                .member(member)
                .token(fcmToken)
                .build();

            memberFcmToken.getFcmTokens().add(newFcmToken);
        }
        memberFcmTokenRepository.save(memberFcmToken);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredFcmTokens() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(60);
        List<MemberFcmToken> allMemberTokens = memberFcmTokenRepository.findAll();

        for (MemberFcmToken memberFcmToken : allMemberTokens) {
            memberFcmToken.getFcmTokens().removeIf(fcmToken ->
                fcmToken.getLastUsedAt().isBefore(expirationDate)
            );

            memberFcmTokenRepository.save(memberFcmToken);
        }
    }

}
