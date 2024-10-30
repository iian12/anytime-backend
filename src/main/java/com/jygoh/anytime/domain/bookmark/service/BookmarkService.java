package com.jygoh.anytime.domain.bookmark.service;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryReqDto;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import java.util.List;

public interface BookmarkService {

    boolean toggleBookmark(Long postId, CategoryReqDto requestDto, String token);

    List<PostSummaryDto> getBookmarkedPostsByCategory(CategoryReqDto requestDto, String token);
}
