package com.jygoh.anytime.domain.like.repository;

import com.jygoh.anytime.domain.like.model.Like;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.post.model.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndPost(Member member, Post post);

}
