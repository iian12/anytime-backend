package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.model.MemberFcmToken;
import com.jygoh.anytime.domain.member.repository.MemberFcmTokenRepository;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberFcmTokenServiceImpl implements MemberFcmTokenService {

    private final MemberRepository memberRepository;
    private MemberFcmTokenRepository memberFcmTokenRepository;

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
                MemberFcmToken newToken = new MemberFcmToken(member, new ArrayList<>());
                return memberFcmTokenRepository.save(newToken);
            });

        memberFcmToken.addToken(fcmToken);
        memberFcmTokenRepository.save(memberFcmToken);
    }

    @Override
    public void removeFcToken(String currentMemberToken, String fcmToken) {

        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(currentMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        MemberFcmToken memberFcmToken = memberFcmTokenRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        memberFcmToken.removeToken(fcmToken);
        memberFcmTokenRepository.save(memberFcmToken);
    }


}
