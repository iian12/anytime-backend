package com.jygoh.anytime.domain.post.dto;

import com.jygoh.anytime.domain.post.model.Post;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostDetailDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private List<String> mediaUrls;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public PostDetailDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getProfileId();
        this.mediaUrls = post.getMediaUrls();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.createdAt = post.getCreatedAt();
    }
}
