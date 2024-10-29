package com.jygoh.anytime.domain.hashtag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HashtagDto {

    private Long id;
    private String name;

    public HashtagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
