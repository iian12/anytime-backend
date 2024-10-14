package com.jygoh.anytime.domain.memo.service;

import com.jygoh.anytime.domain.memo.dto.CreateMemoReqDto;
import com.jygoh.anytime.domain.memo.dto.CreateTeamMemoReqDto;

public interface MemoService {

    void createMemo(CreateMemoReqDto reqDto, String token);

    void createTeamMemo(CreateTeamMemoReqDto reqDto, String token);
}
