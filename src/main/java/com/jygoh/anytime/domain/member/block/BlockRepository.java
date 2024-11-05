package com.jygoh.anytime.domain.member.block;

import com.jygoh.anytime.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);
}
