package com.golmok.golmok_daejang.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class RecommendationWithPersonalityData {
    private String typeA;
    private List<String> storesA;
    private String typeB;
    private List<String> storesB;
    private String typeC;
    private List<String> storesC;
    private Map<String, List<String>> personality;
}
