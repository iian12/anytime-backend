package com.jygoh.anytime.domain.member.dto;

import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileResDto {

    private String profileId;
    private InfoWrapper<String> nickname; // nickname을 InfoWrapper로 감싸기
    private InfoWrapper<String> profileImgUrl; // profileImgUrl을 InfoWrapper로 감싸기
    private InfoWrapper<Integer> followingCount; // followingCount을 InfoWrapper로 감싸기
    private InfoWrapper<Integer> followerCount; // followerCount을 InfoWrapper로 감싸기
    private InfoWrapper<List<PostSummaryDto>> posts; // posts를 InfoWrapper로 감싸기
    private boolean isOwner;
    private boolean isPrivate;
    private boolean isMutualFollow;
    private boolean canSendMessage;
    private InfoWrapper<Integer> postCount;

    @Builder
    public ProfileResDto(String profileId, InfoWrapper<String> nickname,
        InfoWrapper<String> profileImgUrl, InfoWrapper<Integer> followingCount,
        InfoWrapper<Integer> followerCount, InfoWrapper<List<PostSummaryDto>> posts, boolean isOwner, boolean isPrivate,
        boolean isMutualFollow, boolean canSendMessage, InfoWrapper<Integer> postCount) {
        this.profileId = profileId;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
        this.posts = posts;
        this.isOwner = isOwner;
        this.isPrivate = isPrivate;
        this.isMutualFollow = isMutualFollow;
        this.canSendMessage = canSendMessage;
        this.postCount = postCount;
    }

    // InfoWrapper 클래스 추가
    @Getter
    @NoArgsConstructor
    public static class InfoWrapper<T> {

        private T value;
        private boolean isPrivate;

        @Builder
        public InfoWrapper(T value, boolean isPrivate) {
            this.value = value;
            this.isPrivate = isPrivate;
        }
    }
}
