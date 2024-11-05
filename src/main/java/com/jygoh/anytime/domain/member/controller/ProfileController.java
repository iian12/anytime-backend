package com.jygoh.anytime.domain.member.controller;

import com.jygoh.anytime.domain.member.dto.ProfileIdReqDto;
import com.jygoh.anytime.domain.member.dto.ProfileResDto;
import com.jygoh.anytime.domain.member.service.ProfileService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileResDto> getMemberProfile(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        ProfileResDto profileResDto = profileService.getProfile(requestDto.getProfileId(), token);
        return ResponseEntity.ok(profileResDto);
    }

    @GetMapping("/img")
    public ResponseEntity<String> getMyProfileImgUrl(HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);

        String url = profileService.getMyProfileImgUrl(token);

        return ResponseEntity.ok(url);
    }
}
