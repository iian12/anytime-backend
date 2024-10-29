package com.jygoh.anytime.domain.media.controller;

import com.jygoh.anytime.domain.media.service.MediaService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload/single")
    public ResponseEntity<String> uploadSingleMedia(@RequestParam("file") MultipartFile file) {
        try {
            String mediaUrl = mediaService.uploadOneMedia(file);
            return ResponseEntity.ok(mediaUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("파일 업로드 중 오류가 발생했습니다.");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("지원되지 않는 파일 형식입니다. " + e.getMessage());
        }
    }
}
