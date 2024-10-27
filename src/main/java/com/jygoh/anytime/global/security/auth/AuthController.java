package com.jygoh.anytime.global.security.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.io.BaseEncoding;
import com.jygoh.anytime.domain.member.dto.GoogleUserDto;
import com.jygoh.anytime.domain.member.dto.RegisterReqDto;
import com.jygoh.anytime.domain.member.service.MemberService;
import com.jygoh.anytime.global.security.jwt.TokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final MemberService memberService;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    @Value("${client-id}")
    private String CLIENT_ID;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
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
                log.info(profileImageUrl);
                String providerId = payload.getSubject();

                GoogleUserDto userDto = new GoogleUserDto();
                userDto.setEmail(email);
                userDto.setNickname(nickname);
                userDto.setProfileImageUrl(profileImageUrl);
                userDto.setSubjectId(providerId);

                TokenResponseDto tokenResponseDto = memberService.processingGoogleUser(userDto);

                response.setHeader("Authorization", "Bearer " + tokenResponseDto.getAccessToken());
                response.setHeader("Refresh-Token", "Bearer " + tokenResponseDto.getRefreshToken());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (BaseEncoding.DecodingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
