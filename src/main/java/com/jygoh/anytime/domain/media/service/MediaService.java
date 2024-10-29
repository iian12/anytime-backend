package com.jygoh.anytime.domain.media.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {


    String uploadOneMedia(MultipartFile file) throws IOException;


    String uploadAdjustedMedia(MultipartFile multipartFile) throws IOException;
}
