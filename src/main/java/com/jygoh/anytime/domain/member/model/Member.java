package com.jygoh.anytime.domain.member.model;

import com.jygoh.anytime.domain.chat.model.MemberGroupChat;
import com.jygoh.anytime.domain.follow.model.Follow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @OneToMany(mappedBy = "member")
    private List<MemberGroupChat> memberGroupChats = new ArrayList<>();

    private boolean showNickname = true;
    private boolean showProfileImage = true;
    private boolean showFollowingCount = true;
    private boolean showFollowerCount = true;
    private boolean showPostCount = true;

    @Builder(toBuilder = true)
    public Member(String email, String profileId, String password, String nickname, boolean isSignUpComplete,
        boolean isPrivate, int followingCount, List<Follow> followingRelations, int followerCount,
        List<Follow> followerRelations, String profileImageUrl, int postCount, String providerId, String subjectId,
        List<MemberGroupChat> memberGroupChats, boolean showNickname, boolean showProfileImage,
        boolean showFollowingCount, boolean showFollowerCount, boolean showPostCount) {
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
        this.memberGroupChats = memberGroupChats != null ? memberGroupChats : new ArrayList<>();
        this.showNickname = showNickname;
        this.showProfileImage = showProfileImage;
        this.showFollowingCount = showFollowingCount;
        this.showFollowerCount = showFollowerCount;
        this.showPostCount = showPostCount;
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

    public void addFollowing(Member followee) {
        Follow follow = new Follow(this, followee);
        followingRelations.add(follow);
        followingCount++;
        followee.addFollower(this);
    }

    public void removeFollowing(Member followee) {
        Follow follow = followingRelations.stream()
            .filter(f -> f.getFollowee().equals(followee))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("팔로우 관계 없음"));

        followingRelations.remove(follow);
        followingCount--;
        followee.removeFollower(this);
    }

    public void addFollower(Member follower) {
        Follow follow = new Follow(follower, this);
        followerRelations.add(follow);
        followerCount++;
    }

    public void removeFollower(Member follower) {
        Follow follow = followerRelations.stream()
            .filter(f -> f.getFollower().equals(follower))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("팔로우 관계 없음"));

        followerRelations.remove(follow);
        followerCount--;
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
