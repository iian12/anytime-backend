package com.jygoh.anytime.domain.bookmark.category.service;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryResDto;
import com.jygoh.anytime.domain.bookmark.category.dto.CreateCategoryReqDto;
import java.util.List;

public interface CategoryService {

    List<CategoryResDto> getCategoriesByMember(String token);
    Long createCategory(CreateCategoryReqDto requestDto, String token);

}
