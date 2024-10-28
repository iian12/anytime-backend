package com.jygoh.anytime.domain.usertag.service;

import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.usertag.model.UserTag;
import java.util.List;

public interface UserTagService {

    List<UserTag> createUserTags(List<String> userTags, Post post);
}
