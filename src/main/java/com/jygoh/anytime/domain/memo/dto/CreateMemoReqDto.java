package com.jygoh.anytime.domain.memo.dto;

import com.jygoh.anytime.domain.memo.model.Memo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMemoReqDto {

    private String title;
    private String content;

    @Builder
    public CreateMemoReqDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Memo toEntity(Long memberId) {
        return Memo.builder().title(this.title).content(this.content).memberId(memberId).build();
    }
}
