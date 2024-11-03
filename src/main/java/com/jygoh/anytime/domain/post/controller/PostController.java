package com.jygoh.anytime.domain.post.controller;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryReqDto;
import com.jygoh.anytime.domain.bookmark.service.BookmarkService;
import com.jygoh.anytime.domain.post.dto.PostCreateRequestDto;
import com.jygoh.anytime.domain.post.dto.PostDetailDto;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.service.PostService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final BookmarkService bookmarkService;

    public PostController(PostService postService, BookmarkService bookmarkService) {
        this.postService = postService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getPostList(
        @RequestParam(defaultValue = "0") int page, // 페이지 번호 (기본값: 0)
        @RequestParam(defaultValue = "10") int size // 페이지 크기 (기본값: 10)
    ) {
        Page<PostSummaryDto> postList = postService.getPostList(PageRequest.of(page, size));
        return ResponseEntity.ok(postList);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable("postId") Long postId) {
        PostDetailDto postDetail = postService.getPostDetail(postId);
        return ResponseEntity.ok(postDetail);
    }

    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody PostCreateRequestDto requestDto,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        Long createPost = postService.createPost(requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPost);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        boolean liked = postService.toggleLike(postId, token);
        return liked ? ResponseEntity.ok("Liked") : ResponseEntity.ok("Unliked");
    }

    @PostMapping("/bookmarks/{postId}")
    public ResponseEntity<String> toggleBookmark(@PathVariable Long postId,
        @RequestBody CategoryReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        boolean isBookmarked = bookmarkService.toggleBookmark(postId, requestDto, token);
        String message = isBookmarked ? "Bookmark added" : "Bookmark removed";
        return ResponseEntity.ok(message);
    }
}
