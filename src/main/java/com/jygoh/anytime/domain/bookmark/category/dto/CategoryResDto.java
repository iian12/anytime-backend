package com.jygoh.anytime.domain.bookmark.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CategoryResDto {

    private Long categoryId;
    private String categoryName;

    @Builder
    public CategoryResDto(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
