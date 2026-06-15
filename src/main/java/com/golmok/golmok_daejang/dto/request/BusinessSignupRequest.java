package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessSignupRequest {

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("residentNumber")
    private String residentNumber;

    @JsonProperty("businessNumber")
    private String businessNumber;

    @JsonProperty("businessName")
    private String businessName;

    @JsonProperty("businessType")
    private String businessType;

    @JsonProperty("characterFile")
    private String characterFile; // base64 이미지

    @JsonProperty("characterName")
    private String characterName;

    @JsonProperty("rarity")
    private int rarity; // 1~3
}
