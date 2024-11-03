package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.ProfileIdDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.EncryptionUtils;
import com.jygoh.anytime.global.security.jwt.service.JwtTokenProvider;
import com.jygoh.anytime.global.security.jwt.dto.TokenResponseDto;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class MemberServiceImpl implements com.jygoh.anytime.domain.member.service.MemberService {

    private final MemberRepository memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberServiceImpl(MemberRepository memberService,
        BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void register(RegisterReqDto reqDto) {
        if (memberService.existsByEmail(reqDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }
        if (memberService.existsByEmail(reqDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        Optional<Member> existingMember = memberService.findByEmail(reqDto.getEmail());
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (member.getProviderId() != null) {
                throw new IllegalArgumentException("소셜 로그인으로 이미 가입된 사용자입니다. 이메일을 사용할 수 없습니다.");
            }
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(reqDto.getPassword());
        Member member = reqDto.toEntity().toBuilder().password(encodedPassword).build();
        memberService.save(member);
    }

    @Override
    public TokenResponseDto processingGoogleUser(GoogleUserDto dto) {
        Optional<Member> optionalMember = memberService.findByEmail(dto.getEmail());
        TokenResponseDto tokenResponseDto = new TokenResponseDto();

        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();

            String accessToken = jwtTokenProvider.createAccessToken(existingMember.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(existingMember.getId());

            tokenResponseDto.setAccessToken(accessToken);
            tokenResponseDto.setRefreshToken(refreshToken);
        } else {
            Member newMember = Member.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .profileImageUrl(dto.getProfileImageUrl())
                .providerId("Google")
                .subjectId(dto.getSubjectId())
                .build();

            memberService.save(newMember);

           try {
               String encodedMemberId = EncryptionUtils.encrypt(String.valueOf(newMember.getId()));
               tokenResponseDto.setEncodedMemberId(encodedMemberId);
           } catch (Exception e) {
               throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 에러 발생");
           }
        }

        return tokenResponseDto;
    }

    @Override
    public TokenResponseDto setProfileId(ProfileIdDto profileIdDto) {
        if (profileIdDto.getProfileId() == null || profileIdDto.getEncodedMemberId() == null) {
            throw new IllegalArgumentException("Profile ID and Member ID are required");
        }

        boolean isProfileIdAvailable = isProfileIdAvailable(profileIdDto.getProfileId());
        if (!isProfileIdAvailable) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        Long decodedMemberId;

        try {
            decodedMemberId = Long.parseLong(EncryptionUtils.decrypt(profileIdDto.getProfileId()));

            Member member = memberService.findById(decodedMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid MemberID"));

            member.updateProfileId(profileIdDto.getProfileId());
            member.setSignUpComplete(true);

            String accessToken = jwtTokenProvider.createAccessToken(decodedMemberId);
            String refreshToken = jwtTokenProvider.createRefreshToken(decodedMemberId);

            TokenResponseDto tokenResponseDto = new TokenResponseDto();
            tokenResponseDto.setAccessToken(accessToken);
            tokenResponseDto.setRefreshToken(refreshToken);

            return tokenResponseDto;
        } catch (Exception e) {
            throw new RuntimeException("Internal Server Error: " + e.getMessage());
        }
    }

    private boolean isProfileIdAvailable(String profileId) {
        return !memberService.existsByProfileId(profileId);
    }
}
