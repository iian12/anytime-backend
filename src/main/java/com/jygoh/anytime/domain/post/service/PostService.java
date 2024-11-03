package com.jygoh.anytime.domain.post.service;

import com.jygoh.anytime.domain.post.dto.PostCreateRequestDto;
import com.jygoh.anytime.domain.post.dto.PostDetailDto;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Page<PostSummaryDto> getPostList(Pageable pageable);

    PostDetailDto getPostDetail(String postId);

    String createPost(PostCreateRequestDto requestDto, String token);

    boolean toggleLike(String postId, String token);
}
