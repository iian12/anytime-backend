package com.jygoh.anytime.domain.hashtag.controller;

import com.jygoh.anytime.domain.hashtag.dto.HashtagDto;
import com.jygoh.anytime.domain.hashtag.service.HashtagFollowService;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hashtags")
public class HashtagController {

    private final HashtagFollowService hashtagFollowService;

    public HashtagController(HashtagFollowService hashtagFollowService) {
        this.hashtagFollowService = hashtagFollowService;
    }

    @PostMapping("/{hashtagId}/toggle-follow")
    public ResponseEntity<Boolean> toggleFollow(@PathVariable Long hashtagId, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        boolean isFollowing = hashtagFollowService.toggleFollowHashtag(hashtagId, token);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/hashtag-list")
    public ResponseEntity<List<HashtagDto>> getFollowingHashtags(HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        List<HashtagDto> hashtagList = hashtagFollowService.getFollowedHashtag(token);
        return ResponseEntity.ok(hashtagList);
    }
}
