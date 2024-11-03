package com.jygoh.anytime.domain.follow.service;

import com.jygoh.anytime.domain.follow.model.Follow;
import com.jygoh.anytime.domain.follow.model.FollowRequest;
import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRequestRepository;
import com.jygoh.anytime.domain.member.dto.MemberSummaryDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final MemberRepository memberService;
    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;

    public FollowServiceImpl(MemberRepository memberService, FollowRequestRepository followRequestRepository, FollowRepository followRepository) {
        this.memberService = memberService;
        this.followRequestRepository = followRequestRepository;
        this.followRepository = followRepository;
    }

    @Override
    public List<MemberSummaryDto> getFollowingList(String profileId, String token) {
        Member targetMember = memberService.findByProfileId(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member requester = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (targetMember.isPrivate()) {
            boolean isFollower = targetMember.getFollowerRelations().stream()
                .anyMatch(follow -> follow.getFollower().getId().equals(requester.getId()));

            if (!isFollower) {
                throw new AccessDeniedException("비공개 계정입니다.");
            }
        }

        return targetMember.getFollowingRelations().stream().map(Follow::getFollowee).map(
            followee -> MemberSummaryDto.builder().profileId(followee.getProfileId())
                .nickname(followee.getNickname()).profileImgUrl(followee.getProfileImageUrl())
                .isMutualFollow(followRepository.existsByFollowerAndFollowee(followee, requester) &&
                    !followRepository.existsByFollowerAndFollowee(requester, followee))
                .canSendMessage(followRepository.existsByFollowerAndFollowee(followee, requester)
                    && followRepository.existsByFollowerAndFollowee(requester, followee))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<MemberSummaryDto> getFollowerList(String targetProfileId, String requesterToken) {
        Member targetMember = memberService.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member requester = memberService.findById(TokenUtils.getMemberIdFromToken(requesterToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (targetMember.isPrivate()) {
            boolean isFollower = targetMember.getFollowerRelations().stream()
                .anyMatch(follow -> follow.getFollower().getId().equals(requester.getId()));

            if (!isFollower) {
                throw new AccessDeniedException("비공개 계정입니다.");
            }
        }

        return targetMember.getFollowerRelations().stream().map(Follow::getFollower).map(
            follower -> MemberSummaryDto.builder().profileId(follower.getProfileId())
                .nickname(follower.getNickname()).profileImgUrl(follower.getProfileImageUrl())
                .isMutualFollow(followRepository.existsByFollowerAndFollowee(follower, requester) &&
                    !followRepository.existsByFollowerAndFollowee(requester, follower))
                .canSendMessage(followRepository.existsByFollowerAndFollowee(follower, requester)
                && followRepository.existsByFollowerAndFollowee(requester, follower))
                .build()).collect(Collectors.toList());
    }

    @Override
    public String toggleFollow(String targetProfileId, String requesterToken) {
        Long requesterId = TokenUtils.getMemberIdFromToken(requesterToken);
        Member requestMember = memberService.findById(requesterId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member targetMember = memberService.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowee(requestMember, targetMember);

        if (existingFollow.isPresent()) {
            return handleUnfollow(existingFollow.get(), requestMember, targetMember);
        }

        if (targetMember.isPrivate()) {
            return handleFollowRequest(requestMember, targetMember);
        } else {
            return handleFollow(requestMember, targetMember);
        }
    }


    private String handleUnfollow(Follow follow, Member requestMember, Member targetMember) {
        followRepository.delete(follow);
        requestMember.removeFollowingAndFollower(targetMember);
        memberService.save(requestMember);
        return "Unfollowed Successfully";
    }

    private String handleFollowRequest(Member requestMember, Member targetMember) {
        Optional<FollowRequest> existingRequest = followRequestRepository.findByRequesterAndTarget(requestMember, targetMember);

        if (existingRequest.isPresent()) {
            followRequestRepository.delete(existingRequest.get());
            return "Follow request canceled";
        } else {
            FollowRequest followRequest = FollowRequest.builder()
                .requester(requestMember)
                .target(targetMember)
                .build();
            followRequestRepository.save(followRequest);
            return "Follow request sent";
        }
    }

    private String handleFollow(Member requestMember, Member targetMember) {
        Follow follow = Follow.builder()
            .follower(requestMember)
            .followee(targetMember)
            .build();
        followRepository.save(follow);
        requestMember.addFollowingAndFollower(targetMember, follow);
        memberService.save(requestMember);
        return "Followed Successfully";
    }

    @Override
    public void deleteFollower(String targetProfileId, String requesterMemberToken) {
        Member requesterMember = memberService.findById(TokenUtils.getMemberIdFromToken(requesterMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member targetMember = memberService.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (!followRepository.existsByFollowerAndFollowee(requesterMember, targetMember)) {
            throw new IllegalArgumentException("Follower is not followed");
        }

        followRepository.deleteByFollowerAndFollowee(requesterMember, targetMember);
        requesterMember.removeFollowingAndFollower(targetMember);
    }

    @Override
    public String acceptFollowRequest(String requestTerProfileId, String targetMemberToken) {
        Member targetMember = memberService.findById(TokenUtils.getMemberIdFromToken(targetMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requesterMember = memberService.findByProfileId(requestTerProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(requesterMember, targetMember)
            .orElseThrow(() -> new IllegalArgumentException("Follow request not found"));

        followRequest.acceptRequest();

        Follow follow = Follow.builder()
            .follower(requesterMember)
            .followee(targetMember)
            .build();
        followRepository.save(follow);
        followRequestRepository.delete(followRequest);
        return "Follow request accepted";
    }

    @Override
    public String rejectFollowRequest(String requesterProfileId, String targetMemberToken) {
        Member targetMember = memberService.findById(TokenUtils.getMemberIdFromToken(targetMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requesterMember = memberService.findByProfileId(requesterProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(requesterMember,
                targetMember)
            .orElseThrow(() -> new IllegalArgumentException("Follow request not found"));

        followRequest.rejectRequest();
        followRequestRepository.delete(followRequest);
        return "Follow request rejected";
    }
}
