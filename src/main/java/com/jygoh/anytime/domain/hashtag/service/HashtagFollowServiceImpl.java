package com.jygoh.anytime.domain.hashtag.service;

import com.jygoh.anytime.domain.hashtag.dto.HashtagDto;
import com.jygoh.anytime.domain.hashtag.model.Hashtag;
import com.jygoh.anytime.domain.hashtag.model.HashtagFollower;
import com.jygoh.anytime.domain.hashtag.repository.HashtagFollowerRepository;
import com.jygoh.anytime.domain.hashtag.repository.HashtagRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HashtagFollowServiceImpl implements HashtagFollowService {

    private final HashtagFollowerRepository hashtagFollowerRepository;
    private final HashtagRepository hashtagRepository;
    private final MemberRepository memberService;

    public HashtagFollowServiceImpl(HashtagFollowerRepository hashtagFollowerRepository,
        HashtagRepository hashtagRepository, MemberRepository memberService) {
        this.hashtagFollowerRepository = hashtagFollowerRepository;
        this.hashtagRepository = hashtagRepository;
        this.memberService = memberService;
    }


    @Override
    public boolean toggleFollowHashtag(String hashtagId, String token) {
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));
        Hashtag hashtag = hashtagRepository.findById(EncodeDecode.decode(hashtagId))
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
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Member"));

        return hashtagFollowerRepository.findAllByMember(member).stream()
            .map(follower -> new HashtagDto(EncodeDecode.encode(follower.getHashtag().getId()), follower.getHashtag().getName()))
            .collect(Collectors.toList());
    }
}
