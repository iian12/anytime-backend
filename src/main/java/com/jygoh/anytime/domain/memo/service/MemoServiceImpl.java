package com.jygoh.anytime.domain.memo.service;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.memo.dto.CreateMemoReqDto;
import com.jygoh.anytime.domain.memo.dto.CreateTeamMemoReqDto;
import com.jygoh.anytime.domain.memo.repository.MemoRepository;
import com.jygoh.anytime.domain.memo.repository.TeamMemoRepository;
import com.jygoh.anytime.global.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;
    private final TeamMemoRepository teamMemoRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemoServiceImpl(MemoRepository memoRepository, TeamMemoRepository teamMemoRepository,
        MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memoRepository = memoRepository;
        this.teamMemoRepository = teamMemoRepository;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void createMemo(CreateMemoReqDto reqDto, String token) {
        Member member = memberRepository.findById(jwtTokenProvider.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        memoRepository.save(reqDto.toEntity(member.getId()));
    }

    @Override
    public void createTeamMemo(CreateTeamMemoReqDto reqDto, String token) {
        Member member = memberRepository.findById(jwtTokenProvider.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        teamMemoRepository.save(reqDto.toEntity(member.getId()));
    }
}
