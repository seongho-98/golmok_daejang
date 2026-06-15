package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DogamSaveRequest {

    @JsonProperty("id")
    private String loginId;

    @JsonProperty("캐릭터ID")
    private List<Long> characterIds;
}
