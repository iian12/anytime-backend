package com.jygoh.anytime.domain.bookmark.repository;

import com.jygoh.anytime.domain.bookmark.category.model.BookmarkCategory;
import com.jygoh.anytime.domain.bookmark.model.Bookmark;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.post.model.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMember(Member member);
    Optional<Bookmark> findByMemberAndPost(Member member, Post post);
    List<Bookmark> findByMemberAndCategory(Member member, BookmarkCategory category);
}
