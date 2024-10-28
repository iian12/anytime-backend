package com.jygoh.anytime.domain.bookmark;

import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.model.Post;
import java.util.List;

public interface BookmarkService {

    void saveBookmark(Long postId, String token);

    List<PostSummaryDto> getBookmarkedPosts(String token);
}
