package com.jygoh.anytime.domain.post.controller;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryReqDto;
import com.jygoh.anytime.domain.bookmark.service.BookmarkService;
import com.jygoh.anytime.domain.post.dto.PostCreateRequestDto;
import com.jygoh.anytime.domain.post.dto.PostDetailDto;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.service.PostService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
    public ResponseEntity<List<PostDetailDto>> getPostList() {
        List<PostDetailDto> postList = postService.getPostListInMainPage();
        return ResponseEntity.ok(postList);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable("postId") String postId, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        PostDetailDto postDetail = postService.getPostDetail(postId, token);
        return ResponseEntity.ok(postDetail);
    }

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostCreateRequestDto requestDto,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        String createPost = postService.createPost(requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPost);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable String postId,
        HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        boolean liked = postService.toggleLike(postId, token);
        return liked ? ResponseEntity.ok("Liked") : ResponseEntity.ok("Unliked");
    }

    @PostMapping("/bookmarks/{postId}")
    public ResponseEntity<String> toggleBookmark(@PathVariable String postId,
        @RequestBody CategoryReqDto requestDto, HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        boolean isBookmarked = bookmarkService.toggleBookmark(postId, requestDto, token);
        String message = isBookmarked ? "Bookmark added" : "Bookmark removed";
        return ResponseEntity.ok(message);
    }
}
