package com.jygoh.anytime.domain.memo.dto;

import com.jygoh.anytime.domain.memo.model.TeamMemo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTeamMemoReqDto {

    private String title;
    private String content;
    private Long teamId;

    @Builder
    public CreateTeamMemoReqDto(String title, String content, Long teamId) {
        this.title = title;
        this.content = content;
        this.teamId = teamId;
    }

    public TeamMemo toEntity(Long memberId) {
        return TeamMemo.builder().title(this.title).content(this.content).teamId(this.teamId)
            .memberId(memberId).build();
    }
}
