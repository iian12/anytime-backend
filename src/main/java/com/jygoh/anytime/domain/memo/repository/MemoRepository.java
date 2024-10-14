package com.jygoh.anytime.domain.memo.repository;

import com.jygoh.anytime.domain.memo.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {

}
