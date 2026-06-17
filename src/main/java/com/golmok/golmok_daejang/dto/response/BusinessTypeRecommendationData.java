package com.golmok.golmok_daejang.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessTypeRecommendationData {
    private String typeA;
    private List<String> storesA;
    private String typeB;
    private List<String> storesB;
    private String typeC;
    private List<String> storesC;
}
