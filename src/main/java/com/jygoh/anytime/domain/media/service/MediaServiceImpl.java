package com.jygoh.anytime.domain.media.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class MediaServiceImpl implements MediaService {

    @Value("${media.upload-dir}")
    private String uploadDir;

    @Value("${media.temp.upload-dir}")
    private String tempUploadDir;

    @Override
    public String uploadOneMedia(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        boolean isSupportedType = isSupportedFileType(extension);
        if (extension.isEmpty() || !isSupportedType) {
            throw new IllegalArgumentException("지원되지 않는 미디어입니다.");
        }
        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(tempUploadDir + File.separator + fileName);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

        return "http://localhost:8080/media/temp/" + fileName;
    }

    @Override
    public List<String> uploadMultipleMedia(List<MultipartFile> files) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = getFileExtension(file.getOriginalFilename());
            boolean isSupportedType = isSupportedFileType(extension);
            if (extension.isEmpty() || !isSupportedType) {
                throw new IllegalArgumentException("지원되지 않는 미디어입니다.");
            }
            String fileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(tempUploadDir +File.separator + fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);

            fileUrls.add("http://localhost:8080/media.temp/" + fileName);

        }
        return fileUrls;
    }


    @Override
    public String uploadAdjustedMedia(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        boolean isSupportedType = isSupportedFileType(extension);
        if (extension.isEmpty() || !isSupportedType) {
            throw new UnsupportedOperationException("지원되지 않는 미디어입니다.");
        }
        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir, File.separator + fileName);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath.toFile());

        String tempFileUrl = "http://localhost:8080/media/temp/" + fileName;
        Path tempFilePath = Paths.get(tempUploadDir, fileName);

        // 임시 파일이 존재하는 경우 삭제
        if (Files.exists(tempFilePath)) {
            deleteTemporaryMedia(tempFileUrl); // 임시 파일 삭제
        }

        return "http://localhost:8080/media/" + fileName;
    }

    private void deleteTemporaryMedia(String mediaUrl) throws IOException {
        String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(tempUploadDir + File.separator + fileName);
        Files.deleteIfExists(filePath);
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "";
    }

    private boolean isSupportedFileType(String extension) {
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".bmp", ".webp",
            ".mp4", ".mov", ".avi", ".mkv", ".webm", ".gif");
        return allowedExtensions.contains(extension);
    }
}
