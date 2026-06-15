package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("password")
    private String password;
}
