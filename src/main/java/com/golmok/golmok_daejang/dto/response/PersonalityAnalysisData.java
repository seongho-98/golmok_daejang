package com.golmok.golmok_daejang.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PersonalityAnalysisData {
    private Map<String, List<String>> personality; // key: 업종명, value: 형용사 3개
}
