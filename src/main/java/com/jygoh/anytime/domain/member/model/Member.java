package com.jygoh.anytime.domain.member.model;

import com.jygoh.anytime.domain.chat.model.MemberGroupChat;
import com.jygoh.anytime.domain.follow.model.Follow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String profileId;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private boolean isSignUpComplete = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPrivate = false;

    private int followingCount;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingRelations = new ArrayList<>();

    private int followerCount;

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followerRelations = new ArrayList<>();

    private String profileImageUrl;

    private int postCount;

    private String providerId;

    @Column(unique = true)
    private String subjectId;

    @ElementCollection
    private List<Long> chatRoomIds;

    @OneToMany(mappedBy = "member")
    private List<MemberGroupChat> memberGroupChats;

    private boolean showNickname;
    private boolean showProfileImage;
    private boolean showFollowingCount;
    private boolean showFollowerCount;
    private boolean showPostCount;

    @Builder(toBuilder = true)
    public Member(String email, String profileId, String password, String nickname, boolean isSignUpComplete,
        boolean isPrivate, int followingCount, List<Follow> followingRelations, int followerCount,
        List<Follow> followerRelations, String profileImageUrl, int postCount, String providerId, String subjectId, List<Long> chatRoomIds,
        List<MemberGroupChat> memberGroupChats) {
        this.email = email;
        this.profileId = profileId;
        this.password = password;
        this.nickname = nickname;
        this.isSignUpComplete = isSignUpComplete;
        this.isPrivate = isPrivate;
        this.followingCount = followingCount;
        this.followingRelations =
            followingRelations != null ? followingRelations : new ArrayList<>();
        this.followerCount = followerCount;
        this.followerRelations = followerRelations != null ? followerRelations : new ArrayList<>();
        this.profileImageUrl = profileImageUrl;
        this.postCount = postCount;
        this.providerId = providerId;
        this.subjectId = subjectId;
        this.chatRoomIds = chatRoomIds != null ? chatRoomIds : new ArrayList<>();
        this.memberGroupChats = memberGroupChats != null ? memberGroupChats : new ArrayList<>();
        // 기본값 설정
        this.showNickname = true;
        this.showProfileImage = true;
        this.showFollowingCount = true;
        this.showFollowerCount = true;
        this.showPostCount = true;
    }

    public void updateProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void updatePrivacy(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSignUpComplete(boolean isSignUpComplete) {
        this.isSignUpComplete = isSignUpComplete;
    }

    public void updateProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void incrementPostCount() {
        postCount++;
    }

    public void decrementPostCount() {
        if (postCount > 0) {
            postCount--;
        }
    }

    public void addFollowingAndFollower(Member member, Follow follow) {
        followingRelations.add(follow); // 내 팔로우 목록에 추가
        member.getFollowerRelations().add(follow); // 상대의 팔로워 목록에도 추가
        followingCount++;
        member.followerCount++;
    }

    public void removeFollowingAndFollower(Member member) {
        followingRelations.removeIf(f -> f.getFollowee().equals(member));
        member.followerRelations.removeIf(f -> f.getFollower().equals(this));
        followingCount--;
        member.followerCount--;
    }

    // 공개 여부에 따른 정보 반환
    public boolean showNickname() {
        return !isPrivate || showNickname; // 공개 계정이거나 해당 필드가 true인 경우
    }

    public boolean showProfileImg() {
        return !isPrivate || showProfileImage; // 공개 계정이거나 해당 필드가 true인 경우
    }

    public boolean showFollowingCount() {
        return !isPrivate || showFollowingCount; // 공개 계정이거나 해당 필드가 true인 경우
    }

    public boolean showFollowerCount() {
        return !isPrivate || showFollowerCount; // 공개 계정이거나 해당 필드가 true인 경우
    }

    public boolean showPostCount() {
        return !isPrivate || showPostCount; // 공개 계정이거나 해당 필드가 true인 경우
    }
}
