package com.jygoh.anytime.domain.post.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    private String title;
    private String content;
    private List<String> hashtags;
    private List<String> userTags;
    private MultipartFile[] adjustedMediaFiles;

    @Builder
    private PostCreateRequestDto(String title, String content, List<String> hashtags,
        List<String> userTags, MultipartFile[] adjustedMediaFiles) {
        this.title = title;
        this.content = content;
        this.hashtags = hashtags;
        this.userTags = userTags;
        this.adjustedMediaFiles = adjustedMediaFiles;
    }
}
