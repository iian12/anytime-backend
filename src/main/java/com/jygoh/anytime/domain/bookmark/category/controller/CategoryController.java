package com.jygoh.anytime.domain.bookmark.category.controller;

import com.jygoh.anytime.domain.bookmark.category.dto.CategoryResDto;
import com.jygoh.anytime.domain.bookmark.category.service.CategoryService;
import com.jygoh.anytime.global.security.jwt.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmark-categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResDto>> getBookmarkCategories(HttpServletRequest request) {
        String token = TokenUtils.extractTokenFromRequest(request);
        List<CategoryResDto> categories = categoryService.getCategoriesByMember(token);
        return ResponseEntity.ok(categories);
    }

}
