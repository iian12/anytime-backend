package com.jygoh.anytime.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PostSummaryDto {

    private Long id;
    private String thumbnail;

    @Builder
    public PostSummaryDto(Long id, String thumbnail) {
        this.id = id;
        this.thumbnail = thumbnail;
    }
}
