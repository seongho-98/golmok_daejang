package com.golmok.golmok_daejang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessAgentContent {

    private List<String> featureKeywords;
    private List<String> characterNames;
    private String text;
}

