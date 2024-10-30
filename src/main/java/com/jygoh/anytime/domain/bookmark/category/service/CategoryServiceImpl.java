package com.jygoh.anytime.domain.bookmark.category.service;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryResDto;
import com.jygoh.anytime.domain.bookmark.category.dto.CreateCategoryReqDto;
import com.jygoh.anytime.domain.bookmark.category.model.BookmarkCategory;
import com.jygoh.anytime.domain.bookmark.category.repository.CategoryRepository;
import com.jygoh.anytime.domain.member.model.Member;
import com.jygoh.anytime.domain.member.repository.MemberRepository;
import com.jygoh.anytime.global.security.jwt.TokenUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
        MemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<CategoryResDto> getCategoriesByMember(String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));
        return categoryRepository.findAllByMember(member)
            .stream()
            .map(category -> CategoryResDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build()).collect(Collectors.toList());
    }

    @Override
    public Long createCategory(CreateCategoryReqDto requestDto, String token) {
        Member member = memberRepository.findById(TokenUtils.getMemberIdFromToken(token))
            .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

        BookmarkCategory category = BookmarkCategory.builder()
            .member(member)
            .name(requestDto.getCategoryName())
            .build();

        return categoryRepository.save(category).getId();
    }
}
