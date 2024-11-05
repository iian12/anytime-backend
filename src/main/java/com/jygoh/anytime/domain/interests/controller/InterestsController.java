package com.jygoh.anytime.domain.interests.controller;

import com.jygoh.anytime.domain.interests.service.InterestsService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interests")
public class InterestsController {

    private final InterestsService interestsService;

    public InterestsController(InterestsService interestsService) {
        this.interestsService = interestsService;
    }

    @PostMapping
    public ResponseEntity<String> addOrUpdateInterests(@RequestBody List<String> interests, HttpServletRequest request) {

        String token = TokenUtils.extractTokenFromRequest(request);

        try {
            interestsService.addOrUpdateInterests(interests, token);
            return ResponseEntity.ok("관심사 업데이트 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
