package com.jygoh.anytime.global.security.auth;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = getAttribute(oAuth2User, provider, "email");
        String profileImageUrl = getAttribute(oAuth2User, provider, "picture");
        String providerId = getAttribute(oAuth2User, provider, "sub");
        String name = getAttribute(oAuth2User, provider, "name"); // 구글 프로필의 이름 가져오기
        // 기존 회원이 있는지 확인
        Member member = memberRepository.findByEmail(email).map(existingMember -> {
            if (!existingMember.getProviderId().equals(providerId)) {
                // 기존 회원의 providerId 업데이트
                existingMember.updateProviderId(providerId);
                memberRepository.save(existingMember);
            }
            return existingMember;
        }).orElseGet(() -> {
            // 신규 회원 생성
            Member newMember = Member.builder().email(email).nickname(name) // 구글 프로필의 이름으로 닉네임 설정
                .profileImageUrl(profileImageUrl).providerId(providerId).build();
            return memberRepository.save(newMember);
        });
        return new CustomUserDetail(oAuth2User, member.getId());
    }

    private String getAttribute(OAuth2User oAuth2User, String provider, String attributeName) {
        return switch (provider) {
            case "google" -> (String) oAuth2User.getAttribute(attributeName);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
