package com.jygoh.anytime.domain.hashtag.service;

import com.jygoh.anytime.domain.hashtag.dto.HashtagDto;
import com.jygoh.anytime.domain.hashtag.model.Hashtag;
import com.jygoh.anytime.domain.hashtag.model.HashtagFollower;
import com.jygoh.anytime.domain.hashtag.repository.HashtagFollowerRepository;
import com.jygoh.anytime.domain.hashtag.repository.HashtagRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HashtagFollowServiceImpl implements HashtagFollowService {

    private final HashtagFollowerRepository hashtagFollowerRepository;
    private final HashtagRepository hashtagRepository;
    private final MemberRepository memberRepository;

    public HashtagFollowServiceImpl(HashtagFollowerRepository hashtagFollowerRepository,
        HashtagRepository hashtagRepository, MemberRepository memberRepository) {
        this.hashtagFollowerRepository = hashtagFollowerRepository;
        this.hashtagRepository = hashtagRepository;
        this.memberRepository = memberRepository;
    }


    @Override
    public boolean toggleFollowHashtag(Long hashtagId, String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));
        Hashtag hashtag = hashtagRepository.findById(hashtagId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Hashtag"));

        if (hashtagFollowerRepository.existsByMemberAndHashtag(member, hashtag)) {
            hashtagFollowerRepository.deleteByMemberAndHashtag(member, hashtag);
            return false;
        } else {
            HashtagFollower follower = new HashtagFollower(member, hashtag);
            hashtagFollowerRepository.save(follower);
            return true;
        }
    }

    @Override
    public List<HashtagDto> getFollowedHashtag(String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        return hashtagFollowerRepository.findAllByMember(member).stream()
            .map(follower -> new HashtagDto(follower.getHashtag().getId(), follower.getHashtag().getName()))
            .collect(Collectors.toList());
    }
}
