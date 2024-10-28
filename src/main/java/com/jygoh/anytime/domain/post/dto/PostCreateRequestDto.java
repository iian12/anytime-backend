package com.jygoh.anytime.domain.post.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    private String title;
    private String content;
    private List<String> hashtags;
    private List<String> userTags;
    private List<String> mediaUrls;

    @Builder
    private PostCreateRequestDto(String title, String content, List<String> hashtags,
        List<String> userTags, List<String> mediaUrls) {
        this.title = title;
        this.content = content;
        this.hashtags = hashtags;
        this.userTags = userTags;
        this.mediaUrls = mediaUrls;
    }
}
