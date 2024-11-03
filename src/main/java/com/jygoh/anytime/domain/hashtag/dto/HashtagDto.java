package com.jygoh.anytime.domain.hashtag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HashtagDto {

    private String id;
    private String name;

    public HashtagDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
