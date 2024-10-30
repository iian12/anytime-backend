package com.jygoh.anytime.domain.bookmark.category.repository;

import com.jygoh.anytime.domain.bookmark.category.model.BookmarkCategory;
import com.jygoh.anytime.domain.member.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<BookmarkCategory, Long> {

    List<BookmarkCategory> findAllByMember(Member member);
}
