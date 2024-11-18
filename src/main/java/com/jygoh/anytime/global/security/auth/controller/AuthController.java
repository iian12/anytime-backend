package com.jygoh.anytime.global.security.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.io.BaseEncoding;
import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.LoginReqDto;
import com.jygoh.anytime.domain.member.dto.SetProfileIdDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.service.MemberService;
import com.jygoh.anytime.global.security.auth.service.AuthService;
import com.jygoh.anytime.global.security.auth.dto.IdTokenDto;
import com.jygoh.anytime.global.security.jwt.dto.TokenResponseDto;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${client-id}")
    private String CLIENT_ID;

    public AuthController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterReqDto reqDto) {
        try {
            memberService.register(reqDto);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> localLogin(@RequestBody LoginReqDto reqDto, HttpServletResponse response) {

        try {
            TokenResponseDto responseDto = authService.login(reqDto);
            response.setHeader("Authorization", "Bearer " + responseDto.getAccessToken());
            response.setHeader("Refresh-Token", "Bearer" + responseDto.getRefreshToken());
            return ResponseEntity.ok(responseDto);
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/google/login")
    public ResponseEntity<String> loginWithGoogle(@RequestBody IdTokenDto idToken, HttpServletResponse response) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY).setAudience(
                Collections.singletonList(CLIENT_ID)).build();
            String cleanedIdToken = idToken.getIdToken();
            GoogleIdToken token = verifier.verify(cleanedIdToken);
            if (token != null) {
                GoogleIdToken.Payload payload = token.getPayload();
                String email = payload.getEmail();
                String nickname = (String) payload.get("name");
                String profileImageUrl = (String) payload.get("picture");
                String subjectId = payload.getSubject();

                GoogleUserDto userDto = new GoogleUserDto();
                userDto.setEmail(email);
                userDto.setNickname(nickname);
                userDto.setProfileImageUrl(profileImageUrl);
                userDto.setSubjectId(subjectId);

                TokenResponseDto tokenResponseDto = memberService.processingGoogleUser(userDto);
                log.info(tokenResponseDto.getAccessToken());
                if (tokenResponseDto.getAccessToken() != null && tokenResponseDto.getRefreshToken() != null) {
                    response.setHeader("Authorization", "Bearer " + tokenResponseDto.getAccessToken());
                    response.setHeader("Refresh-Token", "Bearer " + tokenResponseDto.getRefreshToken());
                    response.setHeader("Profile-Id", memberService.getProfileId(tokenResponseDto.getAccessToken()));
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED) // HTTP 428
                        .body(tokenResponseDto.getEncodedMemberId());
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (BaseEncoding.DecodingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/set-profile")
    public ResponseEntity<?> setProfileId(@RequestBody SetProfileIdDto setProfileIdDto, HttpServletResponse response) {
        try {
            TokenResponseDto tokenResponseDto = memberService.setProfileId(setProfileIdDto);
            response.setHeader("Authorization", "Bearer" + tokenResponseDto.getAccessToken());
            response.setHeader("Refresh-Token", "Bearer" + tokenResponseDto.getRefreshToken());
            response.setHeader("Profile-Id", memberService.getProfileId(tokenResponseDto.getAccessToken()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/profile-id")
    public ResponseEntity<String> getProfileId(HttpServletRequest request) {
        String profileId = memberService.getProfileId(TokenUtils.extractTokenFromRequest(request));

        return ResponseEntity.ok().body(profileId);
    }
}
