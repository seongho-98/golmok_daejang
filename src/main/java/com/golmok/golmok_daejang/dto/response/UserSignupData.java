package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupData {

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;
}
