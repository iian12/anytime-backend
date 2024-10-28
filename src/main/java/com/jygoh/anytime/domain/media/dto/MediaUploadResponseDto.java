package com.jygoh.anytime.domain.media.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MediaUploadResponseDto {

    private List<String> mediaUrl;
}
