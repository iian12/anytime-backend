package com.jygoh.anytime.domain.post.repository;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.post.model.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    List<Post> findTop10ByAuthorOrderByCreatedAtDesc(Member author);
}
