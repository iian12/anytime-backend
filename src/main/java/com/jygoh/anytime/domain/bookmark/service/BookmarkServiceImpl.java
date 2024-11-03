package com.jygoh.anytime.domain.bookmark.service;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryReqDto;
import com.jygoh.anytime.domain.bookmark.category.model.BookmarkCategory;
import com.jygoh.anytime.domain.bookmark.category.repository.CategoryRepository;
import com.jygoh.anytime.domain.bookmark.model.Bookmark;
import com.jygoh.anytime.domain.bookmark.repository.BookmarkRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.post.repository.PostRepository;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberService;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final EncodeDecode encodeDecode;


    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository,
        MemberRepository memberService, PostRepository postRepository,
        CategoryRepository categoryRepository, EncodeDecode encodeDecode) {
        this.bookmarkRepository = bookmarkRepository;
        this.memberService = memberService;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.encodeDecode = encodeDecode;
    }


    @Override
    public boolean toggleBookmark(String postId, CategoryReqDto requestDto, String token) {
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        Post post = postRepository.findById(encodeDecode.decode(postId))
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        BookmarkCategory category = categoryRepository.findById(requestDto.getCategoryId())
            .orElseGet(() -> {
                BookmarkCategory defaultCategory = BookmarkCategory.builder().name("없음").build();
                return categoryRepository.save(defaultCategory);
            });

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndPost(member, post);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder()
                .member(member)
                .post(post)
                .category(category)
                .build();

            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    @Override
    public List<PostSummaryDto> getBookmarkedPostsByCategory(CategoryReqDto requestDto,
        String token) {
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        BookmarkCategory category = categoryRepository.findById(requestDto.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        List<Bookmark> bookmarks = bookmarkRepository.findByMemberAndCategory(member, category);
        return bookmarks.stream()
            .map(bookmark -> {
                Post post = bookmark.getPost();
                return PostSummaryDto.builder()
                    .id(post.getId())
                    .thumbnail(post.getThumbnail())
                    .build();
            }).toList();
    }
}
