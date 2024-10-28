package com.jygoh.anytime.domain.hashtag.service;

import com.jygoh.anytime.domain.hashtag.model.Hashtag;
import com.jygoh.anytime.domain.hashtag.repository.HashtagRepository;
import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.post.model.PostHashtag;
import com.jygoh.anytime.domain.post.repository.PostHashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public HashtagServiceImpl(HashtagRepository hashtagRepository, PostHashtagRepository postHashtagRepository) {
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
    }


    @Override
    public List<PostHashtag> createHashtags(List<String> hashtags, Post post) {
        if (hashtags == null || hashtags.isEmpty()) {
            return new ArrayList<>();
        }

        return hashtags.stream().map(name -> {
            Hashtag hashtag = hashtagRepository.findByName(name)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(name)));

            PostHashtag postHashtag = new PostHashtag(post, hashtag);
            return postHashtagRepository.save(postHashtag);
        }).collect(Collectors.toList());
    }
}
