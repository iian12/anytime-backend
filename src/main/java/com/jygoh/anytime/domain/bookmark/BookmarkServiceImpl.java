package com.jygoh.anytime.domain.bookmark;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.post.repository.PostRepository;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository,
        MemberRepository memberRepository, PostRepository postRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }


    @Override
    public void saveBookmark(Long postId, String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (bookmarkRepository.existsByMemberAndPost(member, post)) {
            throw new IllegalArgumentException("Post already bookmarked");
        }

        Bookmark bookmark = Bookmark.builder()
            .member(member)
            .post(post)
            .build();

        bookmarkRepository.save(bookmark);
    }

    @Override
    public List<PostSummaryDto> getBookmarkedPosts(String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        return bookmarkRepository.findByMember(member).stream()
            .map(Bookmark::getPost)
            .map(post -> new PostSummaryDto(post.getId(), post.getThumbnail()))
            .collect(Collectors.toList());
    }
}
