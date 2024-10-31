package com.jygoh.anytime.domain.follow.service;

import com.jygoh.anytime.domain.follow.model.Follow;
import com.jygoh.anytime.domain.follow.model.FollowRequest;
import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRequestRepository;
import com.jygoh.anytime.domain.member.dto.MemberSummaryDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final MemberRepository memberRepository;
    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;

    public FollowServiceImpl(MemberRepository memberRepository, FollowRequestRepository followRequestRepository, FollowRepository followRepository) {
        this.memberRepository = memberRepository;
        this.followRequestRepository = followRequestRepository;
        this.followRepository = followRepository;
    }

    @Override
    public List<MemberSummaryDto> getFollowingList(String profileId, String token) {
        Member targetMember = memberRepository.findByProfileId(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
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
    public List<MemberSummaryDto> getFollowerList(String profileId, String token) {
        Member targetMember = memberRepository.findByProfileId(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
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
    public String toggleFollow(String targetProfileId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member target = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (followRepository.existsByFollowerAndFollowee(requester, target)) {
            followRepository.deleteByFollowerAndFollowee(requester, target);
            requester.removeFollowing(target);
            return "Unfollowed successfully";
        }

        if (target.isPrivate()) {
            Optional<FollowRequest> existingRequest =
                followRequestRepository.findByRequesterAndTarget(requester, target);

            if (existingRequest.isPresent()) {
                followRequestRepository.delete(existingRequest.get());
                return "Follow request canceled";
            } else {
                FollowRequest followRequest = FollowRequest.builder()
                    .requester(requester)
                    .target(target)
                    .build();
                followRequestRepository.save(followRequest);
                return "Follow request sent";
            }
        } else {
            Follow follow = Follow.builder()
                .follower(requester)
                .followee(target)
                .build();
            followRepository.save(follow);
            requester.addFollowing(target);
            return "Followed successfully";
        }
    }

    @Override
    public void deleteFollower(String targetProfileId, String token) {
        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member target = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (!followRepository.existsByFollowerAndFollowee(requester, target)) {
            throw new IllegalArgumentException("Follower is not followed");
        }

        followRepository.deleteByFollowerAndFollowee(requester, target);
        requester.removeFollower(target);
    }

    @Override
    public String acceptFollowRequest(String targetProfileId, String token) {
        Member FollowRequestTarget = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requester = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(requester, FollowRequestTarget)
            .orElseThrow(() -> new IllegalArgumentException("Follow request not found"));

        followRequest.acceptRequest();

        Follow follow = Follow.builder()
            .follower(requester)
            .followee(FollowRequestTarget)
            .build();
        followRepository.save(follow);
        followRequestRepository.delete(followRequest);
        return "Follow request accepted";
    }

    @Override
    public String rejectFollowRequest(String targetProfileId, String token) {
        Member FollowRequestTager = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requester = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(requester,
                FollowRequestTager)
            .orElseThrow(() -> new IllegalArgumentException("Follow request not found"));

        followRequest.rejectRequest();
        followRequestRepository.delete(followRequest);
        return "Follow request rejected";
    }
}
