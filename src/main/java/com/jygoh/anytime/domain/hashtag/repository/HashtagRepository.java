package com.jygoh.anytime.domain.hashtag.repository;

import com.jygoh.anytime.domain.hashtag.model.Hashtag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

}
