package com.jygoh.anytime.domain.post.service;

import com.jygoh.anytime.domain.hashtag.service.HashtagService;
import com.jygoh.anytime.domain.like.model.Like;
import com.jygoh.anytime.domain.like.repository.LikeRepository;
import com.jygoh.anytime.domain.media.service.MediaService;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.domain.post.dto.PostCreateRequestDto;
import com.jygoh.anytime.domain.post.dto.PostDetailDto;
import com.jygoh.anytime.domain.post.dto.PostSummaryDto;
import com.jygoh.anytime.domain.post.model.Post;
import com.jygoh.anytime.domain.post.model.PostHashtag;
import com.jygoh.anytime.domain.post.repository.PostRepository;
import com.jygoh.anytime.domain.usertag.model.UserTag;
import com.jygoh.anytime.domain.usertag.service.UserTagService;
import com.jygoh.anytime.global.security.utils.EncodeDecode;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberService;
    private final HashtagService hashtagService;
    private final UserTagService userTagService;
    private final LikeRepository likeRepository;
    private final MediaService mediaService;
    private final EncodeDecode encodeDecode;

    public PostServiceImpl(PostRepository postRepository, MemberRepository memberService,
        HashtagService hashtagService, UserTagService userTagService, LikeRepository likeRepository,
        MediaService mediaService, EncodeDecode encodeDecode) {
        this.postRepository = postRepository;
        this.memberService = memberService;
        this.hashtagService = hashtagService;
        this.userTagService = userTagService;
        this.likeRepository = likeRepository;
        this.mediaService = mediaService;
        this.encodeDecode = encodeDecode;
    }

    @Override
    public Page<PostSummaryDto> getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> new PostSummaryDto(post.getId(), post.getThumbnail()));
    }

    @Override
    public PostDetailDto getPostDetail(String postId) {
        Post post = postRepository.findById(encodeDecode.decode(postId))
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.incrementViewCount();
        return new PostDetailDto(post);
    }

    @Override
    public String createPost(PostCreateRequestDto requestDto, String token) {
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        List<String> permanentMediaUrls = new ArrayList<>();
        MultipartFile[] adjustedMediaFiles = requestDto.getAdjustedMediaFiles();

        if (adjustedMediaFiles != null) { // null 확인 후 반복문 실행
            for (MultipartFile file : adjustedMediaFiles) {
                if (file != null) { // null 파일 처리
                    try {
                        String mediaUrl = mediaService.uploadAdjustedMedia(file);
                        permanentMediaUrls.add(mediaUrl);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("미디어 파일 저장 중 오류가 발생했습니다.");
                    }
                } else {
                    throw new IllegalArgumentException("미디어 파일 저장 중 오류가 발생했습니다.");
                }
            }
        }

        Post post = Post.builder().title(requestDto.getTitle()).content(requestDto.getContent())
            .author(member).mediaUrls(permanentMediaUrls).likeCount(0).commentCount(0)
            .reportCount(0).build();

        postRepository.save(post);
        member.incrementPostCount();

        List<PostHashtag> postHashtags = hashtagService.createHashtags(
            requestDto.getHashtags(), post);
        List<UserTag> userTags = userTagService.createUserTags(requestDto.getUserTags(), post);
        post.addDetails(postHashtags, userTags);

        return encodeDecode.encode(post.getId());
    }

    @Override
    public boolean toggleLike(String postId, String token) {
        Member member = memberService.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        Post post = postRepository.findById(encodeDecode.decode(postId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Post"));

        Optional<Like> existingLike = likeRepository.findByMemberAndPost(member, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decrementLikeCount();
            return false;
        } else {
            likeRepository.save(new Like(member, post));
            post.incrementLikeCount();
            return true;
        }
    }
}
