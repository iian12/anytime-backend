package com.jygoh.anytime.domain.follow.controller;

import com.jygoh.anytime.domain.follow.service.FollowService;
import com.jygoh.anytime.domain.member.dto.MemberSummaryDto;
import com.jygoh.anytime.domain.member.dto.ProfileIdReqDto;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/following")
    public ResponseEntity<List<MemberSummaryDto>> getFollowing(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        List<MemberSummaryDto> dtoList = followService.getFollowingList(requestDto.getProfileId(), token);
        return ResponseEntity.ok().body(dtoList);
    }

    @GetMapping("/follower")
    public ResponseEntity<List<MemberSummaryDto>> getFollower(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        List<MemberSummaryDto> dtoList = followService.getFollowerList(requestDto.getProfileId(), token);
        return ResponseEntity.ok().body(dtoList);
    }

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleFollow(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        String response = followService.toggleFollow(requestDto.getProfileId(), token); // 팔로이, 팔로워
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-follower")
    public ResponseEntity<String> deleteFollower(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        followService.deleteFollower(requestDto.getProfileId(), token); // 삭제 대상, 사용자
        return ResponseEntity.ok("success");
    }

    @PostMapping("/accept")
    public ResponseEntity<String> accept(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        String response = followService.acceptFollowRequest(requestDto.getProfileId(), token); // 팔로워, 팔로이
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody ProfileIdReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        String response = followService.rejectFollowRequest(requestDto.getProfileId(), token); // 팔로워(삭제 대상), 팔로이(사용자)
        return ResponseEntity.ok(response);
    }
}
