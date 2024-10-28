package com.jygoh.anytime.domain.hashtag.service;


import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.post.model.PostHashtag;
import java.util.List;

public interface HashtagService {

    List<PostHashtag> createHashtags(List<String> hashtags, Post post);
}
