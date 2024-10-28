package com.jygoh.anytime.domain.media.controller;

import com.jygoh.anytime.domain.media.service.MediaService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/upload/multiple")
    public ResponseEntity<List<String>> uploadMultipleMedia(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> mediaUrls = mediaService.uploadMedia(files);
            return ResponseEntity.ok(mediaUrls);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList("파일 업로드 중 오류가 발생했습니다."));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonList("지원되지 않는 파일 형식이 포함되어 있습니다.: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTemporaryMedia(@RequestParam("url") String mediaUrl) {
        try {
            mediaService.deleteTemporaryFile(mediaUrl);
            return ResponseEntity.ok("임시 미디어가 삭제되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("임시 파일 삭제 중 오류가 발생했습니다.");
        }
    }
}
