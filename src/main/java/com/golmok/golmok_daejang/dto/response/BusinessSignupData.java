package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessSignupData {

    @JsonProperty("아이디")
    private String loginId;

    @JsonProperty("사업자번호")
    private String businessNumber;

    @JsonProperty("캐릭터")
    private CharacterInfo character;

    @Getter
    @Builder
    public static class CharacterInfo {

        @JsonProperty("캐릭터ID")
        private Long characterId;

        @JsonProperty("이름")
        private String name;

        @JsonProperty("imgUrl")
        private String imgUrl;

        @JsonProperty("희귀도")
        private int rarity;
    }
}
