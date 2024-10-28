package com.jygoh.anytime.domain.bookmark;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.post.model.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByMemberAndPost(Member member, Post post);
    Optional<Bookmark> findByMember(Member member);
}
