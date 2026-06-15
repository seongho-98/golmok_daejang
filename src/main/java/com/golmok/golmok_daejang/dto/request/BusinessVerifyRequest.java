package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessVerifyRequest {

    @JsonProperty("이름")
    private String name;

    @JsonProperty("주민번호")
    private String residentNumber;
}
