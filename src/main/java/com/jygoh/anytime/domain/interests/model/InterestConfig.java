package com.jygoh.anytime.domain.interests.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InterestConfig {

    // 주제별 관심사 목록
    private static final Map<String, List<String>> INTERESTS_MAP;

    static {
        INTERESTS_MAP = new LinkedHashMap<>();

        INTERESTS_MAP.put("게임 및 엔터테인먼트", List.of(
            "애니",
            "만화",
            "보드게임",
            "웹툰",
            "역사",
            "게임",
            "아이돌"
        ));

        INTERESTS_MAP.put("기술 및 과학", List.of(
            "블록체인",
            "로봇",
            "프로그래밍",
            "자연",
            "인공지능",
            "우주"
        ));

        INTERESTS_MAP.put("취미 및 여가", List.of(
            "드로잉",
            "독서",
            "맛집 탐방",
            "베이킹",
            "사진",
            "음악",
            "악기",
            "요리",
            "뜨개질"
        ));

        INTERESTS_MAP.put("사회 및 정치", List.of(
            "국제",
            "인권",
            "사회",
            "정치"
        ));

        INTERESTS_MAP.put("운동 및 건강", List.of(
            "농구",
            "헬스",
            "자전거",
            "수영",
            "축구",
            "야구",
            "요가",
            "등산"
        ));

        INTERESTS_MAP.put("비즈니스 및 금융", List.of(
            "투자",
            "네트워킹",
            "마케팅",
            "비즈니스",
            "창업"
        ));

        INTERESTS_MAP.put("학습 및 자기계발", List.of(
            "명상",
            "외국어",
            "자기계발",
            "자격증",
            "시간 관리"
        ));

        INTERESTS_MAP.put("여행", List.of(
            "배낭여행",
            "국내 여행",
            "문화 탐방",
            "음식 여행",
            "자연 탐험",
            "캠핑",
            "낚시"
        ));

        INTERESTS_MAP.put("예술 및 문화", List.of(
            "미술",
            "무용",
            "클래식",
            "영화",
            "연극"
        ));
    }

    public static Map<String, List<String>> getInterestsMap() {
        return Collections.unmodifiableMap(INTERESTS_MAP);
    }
}
