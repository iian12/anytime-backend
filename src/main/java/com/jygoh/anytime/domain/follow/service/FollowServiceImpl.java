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
    public List<MemberSummaryDto> getFollowerList(String targetProfileId, String requesterToken) {
        Member targetMember = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member requester = memberRepository.findById(TokenUtils.getMemberIdFromToken(requesterToken))
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
        Member requesterMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(requesterToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member targetMember = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (followRepository.existsByFollowerAndFollowee(requesterMember, targetMember)) {
            followRepository.deleteByFollowerAndFollowee(requesterMember, targetMember);
            requesterMember.removeFollowing(targetMember);
            return "Unfollowed successfully";
        }

        if (targetMember.isPrivate()) {
            Optional<FollowRequest> existingRequest =
                followRequestRepository.findByRequesterAndTarget(requesterMember, targetMember);

            if (existingRequest.isPresent()) {
                followRequestRepository.delete(existingRequest.get());
                return "Follow request canceled";
            } else {
                FollowRequest followRequest = FollowRequest.builder()
                    .requester(requesterMember)
                    .target(targetMember)
                    .build();
                followRequestRepository.save(followRequest);
                return "Follow request sent";
            }
        } else {
            Follow follow = Follow.builder()
                .follower(requesterMember)
                .followee(targetMember)
                .build();
            followRepository.save(follow);
            requesterMember.addFollowing(targetMember);
            return "Followed successfully";
        }
    }

    @Override
    public void deleteFollower(String targetProfileId, String requesterToken) {
        Member requesterMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(requesterToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member targetMember = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        if (!followRepository.existsByFollowerAndFollowee(requesterMember, targetMember)) {
            throw new IllegalArgumentException("Follower is not followed");
        }

        followRepository.deleteByFollowerAndFollowee(requesterMember, targetMember);
        requesterMember.removeFollower(targetMember);
    }

    @Override
    public String acceptFollowRequest(String requestTerProfileId, String targetMemberToken) {
        Member targetMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(targetMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requesterMember = memberRepository.findByProfileId(requestTerProfileId)
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
        Member targetMember = memberRepository.findById(TokenUtils.getMemberIdFromToken(targetMemberToken))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        Member requesterMember = memberRepository.findByProfileId(requesterProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        FollowRequest followRequest = followRequestRepository.findByRequesterAndTarget(requesterMember,
                targetMember)
            .orElseThrow(() -> new IllegalArgumentException("Follow request not found"));

        followRequest.rejectRequest();
        followRequestRepository.delete(followRequest);
        return "Follow request rejected";
    }
}
