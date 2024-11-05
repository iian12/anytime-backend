package com.jygoh.anytime.domain.member.service;

import com.jygoh.anytime.domain.member.dto.ProfileResDto;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.repository.PostRepository;
import com.jygoh.anytime.global.security.jwt.utils.BlockValidator;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final MemberRepository memberService;
    private final PostRepository postRepository;
    private final BlockValidator blockValidator;

    public ProfileServiceImpl(MemberRepository memberService, PostRepository postRepository, BlockValidator blockValidator) {
        this.memberService = memberService;
        this.postRepository = postRepository;
        this.blockValidator = blockValidator;
    }

    @Override
    public ProfileResDto getProfile(String profileId, String token) {
        Member member = memberService.findByProfileId(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid ProfileId"));

        Long requesterId = TokenUtils.getMemberIdFromToken(token);

        Member requester = memberService.findById(requesterId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        blockValidator.validateNotBlocked(requester, member);

        ProfileResDto.InfoWrapper<String> nicknameInfo;
        ProfileResDto.InfoWrapper<String> profileImgUrlInfo;
        ProfileResDto.InfoWrapper<Integer> followingCountInfo;
        ProfileResDto.InfoWrapper<Integer> followerCountInfo;
        ProfileResDto.InfoWrapper<Integer> postCountInfo;
        ProfileResDto.InfoWrapper<List<PostSummaryDto>> postsInfo;

        boolean isFollower = member.getFollowerRelations().stream()
            .anyMatch(follow -> follow.getFollower().getId().equals(requesterId));

        boolean isMutualFollow = !isFollower && requester.getFollowerRelations().stream()
            .anyMatch(follow -> follow.getFollower().getId().equals(member.getId()));
        boolean canSendMessage = isFollower && requester.getFollowerRelations().stream()
            .anyMatch(follow -> follow.getFollower().getId().equals(member.getId()));

        if (!member.isPrivate() && !requesterId.equals(member.getId())) { // 공개 계정이며 다른 사용자의 프로필
            nicknameInfo = new ProfileResDto.InfoWrapper<>(member.getNickname(), false);
            profileImgUrlInfo = new ProfileResDto.InfoWrapper<>(member.getProfileImageUrl(), false);
            followingCountInfo = new ProfileResDto.InfoWrapper<>(member.getFollowingCount(), false);
            followerCountInfo = new ProfileResDto.InfoWrapper<>(member.getFollowerCount(), false);
            postCountInfo = new ProfileResDto.InfoWrapper<>(member.getPostCount(), false);

            List<PostSummaryDto> posts = postRepository.findTop10ByAuthorOrderByCreatedAtDesc(
                member).stream().map(
                post -> PostSummaryDto.builder().id(post.getId()).thumbnail(post.getThumbnail())
                    .build()).toList();

            postsInfo = new ProfileResDto.InfoWrapper<>(posts, false);

            return ProfileResDto.builder().profileId(member.getProfileId()).nickname(nicknameInfo)
                .profileImgUrl(profileImgUrlInfo).followingCount(followingCountInfo)
                .followerCount(followerCountInfo).posts(postsInfo).isOwner(false).isPrivate(false)
                .isMutualFollow(isMutualFollow).canSendMessage(canSendMessage)
                .postCount(postCountInfo).build();

        } else if (requesterId.equals(member.getId()) || isFollower) { // 자신의 프로필이거나 팔로워일 경우
            nicknameInfo = new ProfileResDto.InfoWrapper<>(member.getNickname(), false);
            profileImgUrlInfo = new ProfileResDto.InfoWrapper<>(member.getProfileImageUrl(), false);
            followingCountInfo = new ProfileResDto.InfoWrapper<>(member.getFollowingCount(), false);
            followerCountInfo = new ProfileResDto.InfoWrapper<>(member.getFollowerCount(), false);
            postCountInfo = new ProfileResDto.InfoWrapper<>(member.getPostCount(), false);

            List<PostSummaryDto> posts = postRepository.findTop10ByAuthorOrderByCreatedAtDesc(
                member).stream().map(
                post -> PostSummaryDto.builder().id(post.getId()).thumbnail(post.getThumbnail())
                    .build()).toList();

            postsInfo = new ProfileResDto.InfoWrapper<>(posts, false);

            return ProfileResDto.builder()
                .profileId(member.getProfileId())
                .nickname(nicknameInfo)
                .profileImgUrl(profileImgUrlInfo)
                .followingCount(followingCountInfo)
                .followerCount(followerCountInfo)
                .posts(postsInfo)
                .isOwner(requesterId.equals(member.getId()))
                .isPrivate(false)
                .isMutualFollow(isMutualFollow)
                .canSendMessage(canSendMessage)
                .postCount(postCountInfo)
                .build();

        } else { // 비공개 계정이며 팔로워가 아닌 경우 제한된 정보만 반환
            nicknameInfo = new ProfileResDto.InfoWrapper<>(
                member.showNickname() ? member.getNickname() : null, !member.showNickname());
            profileImgUrlInfo = new ProfileResDto.InfoWrapper<>(
                member.showProfileImg() ? member.getProfileImageUrl() : null,
                !member.showProfileImg());
            followingCountInfo = new ProfileResDto.InfoWrapper<>(
                member.showFollowingCount() ? member.getFollowingCount() : null,
                !member.showFollowingCount());
            followerCountInfo = new ProfileResDto.InfoWrapper<>(
                member.showFollowerCount() ? member.getFollowerCount() : null,
                !member.showFollowerCount());
            postCountInfo = new ProfileResDto.InfoWrapper<>(
                member.showPostCount() ? member.getPostCount() : null, !member.showPostCount());
            postsInfo = new ProfileResDto.InfoWrapper<>(new ArrayList<>(), true); // 빈 리스트 및 private 설정

            return ProfileResDto.builder()
                .profileId(member.getProfileId())
                .nickname(nicknameInfo)
                .profileImgUrl(profileImgUrlInfo)
                .followingCount(followingCountInfo)
                .followerCount(followerCountInfo)
                .posts(postsInfo)
                .isOwner(false)
                .isPrivate(true)
                .isMutualFollow(isMutualFollow)
                .canSendMessage(false)
                .postCount(postCountInfo)
                .build();
        }
    }
}
