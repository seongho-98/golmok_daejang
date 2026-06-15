package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessVerifyRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("residentNumber")
    private String residentNumber;
}
