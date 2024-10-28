package com.jygoh.anytime.domain.post.model;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.usertag.model.UserTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTag> userTags;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String thumbnail;

    @ElementCollection
    private List<String> mediaUrls;

    private int viewCount;
    private int likeCount;
    private int commentCount;
    private int reportCount;

    @Builder
    public Post(String title, String content, Member author, List<PostHashtag> postHashtags,
        List<UserTag> userTags, List<String> mediaUrls, int viewCount, int likeCount,
        int commentCount, int reportCount) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.postHashtags =
            postHashtags != null ? new ArrayList<>(postHashtags) : new ArrayList<>();
        this.userTags = userTags != null ? new ArrayList<>(userTags) : new ArrayList<>();
        this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
        this.thumbnail = !this.mediaUrls.isEmpty() ? this.mediaUrls.get(0) : null;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.reportCount = reportCount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addDetails(List<PostHashtag> postHashtags, List<UserTag> userTags) {
        if (postHashtags != null) {
            this.postHashtags.addAll(postHashtags);
        }
        if (userTags != null) {
            this.userTags.addAll(userTags);
        }
    }

    public void updatePost(String title, String content, List<PostHashtag> updatedPostHashtags,
        List<UserTag> updatedUserTags, List<String> updatedMediaUrls) {
        if (title != null) {
            this.title = title;
        }

        if (content != null) {
            this.content = content;
        }

        if (updatedPostHashtags != null) {
            Set<PostHashtag> newHashtags = new HashSet<>(updatedPostHashtags);
            Set<PostHashtag> currentHashtags = new HashSet<>(this.postHashtags);

            for (PostHashtag hashtag : currentHashtags) {
                if (!newHashtags.contains(hashtag)) {
                    this.postHashtags.remove(hashtag);
                }
            }

            for (PostHashtag hashtag : newHashtags) {
                if (!currentHashtags.contains(hashtag)) {
                    this.postHashtags.add(hashtag);
                }
            }
        }

        if (updatedUserTags != null) {
            Set<UserTag> newTags = new HashSet<>(updatedUserTags);
            Set<UserTag> currentTags = new HashSet<>(this.userTags);

            for (UserTag tag : currentTags) {
                if (!newTags.contains(tag)) {
                    this.userTags.remove(tag);
                }
            }

            for (UserTag tag : newTags) {
                if (!currentTags.contains(tag)) {
                    this.userTags.add(tag);
                }
            }
        }
        if (updatedMediaUrls != null) {
            this.mediaUrls.clear();
            this.mediaUrls.addAll(updatedMediaUrls);

            this.thumbnail = !updatedMediaUrls.isEmpty() ? updatedMediaUrls.get(0) : null;
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
