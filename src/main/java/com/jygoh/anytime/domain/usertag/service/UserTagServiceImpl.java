package com.jygoh.anytime.domain.usertag.service;

import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.usertag.model.UserTag;
import com.jygoh.anytime.domain.usertag.repository.UserTagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserTagServiceImpl implements UserTagService {

    private final MemberRepository memberRepository;
    private final UserTagRepository userTagRepository;

    public UserTagServiceImpl(MemberRepository memberRepository, UserTagRepository userTagRepository) {
        this.memberRepository = memberRepository;
        this.userTagRepository = userTagRepository;
    }


    @Override
    public List<UserTag> createUserTags(List<String> userTags, Post post) {
        if (userTags == null || userTags.isEmpty()) {
            return new ArrayList<>();
        }

        return userTags.stream().map(taggedUserProfileId -> {
            Member taggedUser = memberRepository.findByProfileId(taggedUserProfileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            UserTag userTag = new UserTag(post, taggedUser);
            return userTagRepository.save(userTag);
        }).collect(Collectors.toList());
    }
}
