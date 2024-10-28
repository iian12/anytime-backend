package com.jygoh.anytime.domain.media.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    String uploadOneMedia(MultipartFile file) throws IOException;

    List<String> uploadMedia(MultipartFile[] files) throws IOException;

    void deleteTemporaryFile(String mediaUrl) throws IOException;

    void moveMediaToPermanent(List<String> mediaUrls) throws IOException;

    void deleteTempMedia(List<String> mediaUrls) throws IOException;
}
