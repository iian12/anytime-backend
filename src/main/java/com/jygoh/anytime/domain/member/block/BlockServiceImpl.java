package com.jygoh.anytime.domain.member.block;

import com.jygoh.anytime.domain.follow.repository.FollowRepository;
import com.jygoh.anytime.domain.follow.repository.FollowRequestRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;

    public BlockServiceImpl(BlockRepository blockRepository, MemberRepository memberRepository,
        FollowRepository followRepository, FollowRequestRepository followRequestRepository) {
        this.blockRepository = blockRepository;
        this.memberRepository = memberRepository;
        this.followRepository = followRepository;
        this.followRequestRepository = followRequestRepository;
    }

    @Override
    public boolean toggleBlockMember(String targetProfileId, String token) {

        Member blocker = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        Member blocked = memberRepository.findByProfileId(targetProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(() -> new IllegalArgumentException("차단된 사용자가 아님."));
            blockRepository.delete(block);
            return false;
        } else {
            Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

            followRepository.deleteByFollowerAndFollowee(blocker, blocked);
            followRepository.deleteByFollowerAndFollowee(blocked, blocker);

            followRequestRepository.deleteByRequesterAndTarget(blocker, blocked);
            followRequestRepository.deleteByRequesterAndTarget(blocked, blocker);

            blockRepository.save(block);
            return true;
        }
    }
}
