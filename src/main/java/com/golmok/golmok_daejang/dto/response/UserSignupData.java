package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupData {

    @JsonProperty("아이디")
    private String loginId;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("타입")
    private String type;
}
