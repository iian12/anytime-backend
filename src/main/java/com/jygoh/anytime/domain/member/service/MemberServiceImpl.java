package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public void register(RegisterReqDto reqDto) {
        if (memberRepository.existsByEmail(reqDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }
        if (memberRepository.existsByEmail(reqDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        Optional<Member> existingMember = memberRepository.findByEmail(reqDto.getEmail());
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (member.getProviderId() != null) {
                throw new IllegalArgumentException("소셜 로그인으로 이미 가입된 사용자입니다. 이메일을 사용할 수 없습니다.");
            }
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(reqDto.getPassword());
        Member member = reqDto.toEntity().toBuilder().password(encodedPassword).build();
        memberRepository.save(member);
    }

    @Override
    public Member processingGoogleUser(GoogleUserDto dto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(dto.getEmail());

        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            existingMember.updateProviderId(dto.getProviderId());
            return memberRepository.save(existingMember);
        } else {
            Member newMember = Member.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .profileImageUrl(dto.getProfileImageUrl())
                .providerId(dto.getProviderId())
                .build();
            return memberRepository.save(newMember);
        }
    }
}
