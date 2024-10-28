package com.jygoh.anytime.domain.media.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

    private final List<String> temporaryFiles = new ArrayList<>();

    @Override
    public String uploadOneMedia(MultipartFile file) throws IOException {   // 단일 미디어 업로드 ex) 스토리
        String extension = getFileExtension(file.getOriginalFilename());

        if (extension.isEmpty()) {
            throw new UnsupportedOperationException(file.getOriginalFilename());
        }

        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir + File.separator + fileName);
        Files.createDirectories(filePath.getParent());

        file.transferTo(filePath.toFile());
        String mediaUrl = "/media/temp/" + fileName;
        temporaryFiles.add(mediaUrl);
        return mediaUrl;
    }

    @Override
    public List<String> uploadMedia(MultipartFile[] files) throws IOException {     // 다중 미디어 업로드 ex) 게시물
        List<String> mediaUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = getFileExtension(file.getOriginalFilename());

            if (extension.isEmpty()) {
                throw new UnsupportedOperationException("지원되지 않는 미디어 타입입니다." + file.getOriginalFilename());
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadDir + File.separator + fileName);
            Files.createDirectories(filePath.getParent());

            file.transferTo(filePath.toFile());

            String mediaUrl = "/media/" + fileName;
            mediaUrls.add(mediaUrl);
        }
        return mediaUrls;
    }

    @Override
    public void deleteTemporaryFile(String mediaUrl) throws IOException {
        String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(uploadDir + File.separator + fileName);

        Files.deleteIfExists(filePath);
        temporaryFiles.remove(mediaUrl);
    }

    @Override
    public void moveMediaToPermanent(List<String> mediaUrls) throws IOException {
        for (String mediaUrl : mediaUrls) {
            String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
            Path tempFilePath = Paths.get(tempUploadDir + File.separator + fileName);
            Path permanentFilePath = Paths.get(uploadDir + File.separator + fileName);

            Files.move(tempFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void deleteTempMedia(List<String> mediaUrls) throws IOException {
        for (String mediaUrl : mediaUrls) {
            String fileName = mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1);
            Path tempFilePath = Paths.get(tempUploadDir + File.separator + fileName);

            Files.deleteIfExists(tempFilePath);
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return "";
    }
}
