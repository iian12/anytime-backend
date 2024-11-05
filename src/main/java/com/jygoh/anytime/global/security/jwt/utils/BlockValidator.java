package com.jygoh.anytime.global.security.jwt.utils;

import com.jygoh.anytime.domain.member.block.BlockRepository;
import com.jygoh.anytime.domain.member.model.Member;
import org.springframework.stereotype.Component;

@Component
public class BlockValidator {

    private final BlockRepository blockRepository;
    
    public BlockValidator(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }
    
    public void validateNotBlocked(Member requester, Member target) {
        if (blockRepository.existsByBlockerAndBlocked(target, requester)) {
            throw new IllegalArgumentException("차단된 사용자에게는 요청을 보낼 수 없습니다.");
        }
    }
}
