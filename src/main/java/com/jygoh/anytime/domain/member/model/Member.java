package com.jygoh.anytime.domain.member.model;

import com.jygoh.anytime.domain.chat.model.MemberGroupChat;
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

    private String providerId;

    @Column(unique = true)
    private String subjectId;

    @OneToMany(mappedBy = "member")
    private List<MemberGroupChat> memberGroupChats = new ArrayList<>();

    @Builder(toBuilder = true)
    public Member(String email, String profileId, String password, String nickname, boolean isSignUpComplete,
        boolean isPrivate, int followingCount, List<Follow> followingRelations, int followerCount,
        List<Follow> followerRelations, String profileImageUrl, String providerId, String subjectId,
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
        this.providerId = providerId;
        this.subjectId = subjectId;
        this.memberGroupChats = memberGroupChats != null ? memberGroupChats : new ArrayList<>();
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

    public void addFollowing(Member followee) {
        Follow follow = new Follow(this, followee);
        followingRelations.add(follow);
        followingCount++;
        followee.addFollower(this);
    }

    public void removeFollowing(Member followee) {
        Follow follow = new Follow(this, followee);
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
        Follow follow = new Follow(follower, this);
        followerRelations.remove(follow);
        followerCount--;
    }
}
