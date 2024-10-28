package com.jygoh.anytime.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSummaryDto {

    private Long id;
    private String thumbnail;

    public PostSummaryDto(Long id, String thumbnail) {
        this.id = id;
        this.thumbnail = thumbnail;
    }
}
