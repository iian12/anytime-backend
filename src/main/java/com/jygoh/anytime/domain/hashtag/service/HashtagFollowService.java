package com.jygoh.anytime.domain.hashtag.service;

import com.jygoh.anytime.domain.hashtag.dto.HashtagDto;
import java.util.List;

public interface HashtagFollowService {

    boolean toggleFollowHashtag(String hashtagId, String token);

    List<HashtagDto> getFollowedHashtag(String token);
}
