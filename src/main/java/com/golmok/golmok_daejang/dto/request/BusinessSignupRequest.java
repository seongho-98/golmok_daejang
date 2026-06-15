package com.golmok.golmok_daejang.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessSignupRequest {

    @JsonProperty("아이디")
    private String loginId;

    @JsonProperty("비번")
    private String password;

    @JsonProperty("성명")
    private String name;

    @JsonProperty("주민번호")
    private String residentNumber;

    @JsonProperty("사업자번호")
    private String businessNumber;

    @JsonProperty("업체명")
    private String businessName;

    @JsonProperty("업태명")
    private String businessType;

    @JsonProperty("캐릭터파일")
    private String characterFile; // base64 이미지

    @JsonProperty("캐릭터명")
    private String characterName;

    @JsonProperty("희귀도")
    private int rarity; // 1~3
}
