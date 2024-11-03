package com.jygoh.anytime.global.security.auth.service;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberService;

    public CustomUserDetailsService(MemberRepository memberService) {
        this.memberService = memberService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // CustomUserDetail 생성 시, Member 객체와 Member ID를 전달
        return new CustomUserDetail(member, member.getId());
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Member member = memberService.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return new CustomUserDetail(member, member.getId());
    }
}
