package com.jygoh.anytime.domain.interests.service;

import com.jygoh.anytime.domain.interests.model.InterestConfig;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InterestsServiceImpl implements InterestsService {

    private final MemberRepository memberRepository;

    public InterestsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void addOrUpdateInterests(List<String> interests, String token) {
        Map<String, List<String>> predefinedInterests = InterestConfig.getInterestsMap();

        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        List<String> currentInterests = member.getInterests();

        List<String> validInterests = new ArrayList<>();

        for (String interest : interests) {
            if (predefinedInterests.values().stream().anyMatch(list -> list.contains(interest))) {
                validInterests.add(interest);
            }
        }

        if (validInterests.size() > 5) {
            throw new IllegalArgumentException("You have reached the maximum 5 interests");
        }

        currentInterests.clear();
        currentInterests.addAll(validInterests);
    }
}
