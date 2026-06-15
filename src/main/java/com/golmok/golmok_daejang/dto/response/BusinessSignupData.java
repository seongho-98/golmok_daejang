package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessSignupData {

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("businessNumber")
    private String businessNumber;

    @JsonProperty("character")
    private CharacterInfo character;

    @Getter
    @Builder
    public static class CharacterInfo {

        @JsonProperty("characterId")
        private Long characterId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("imgUrl")
        private String imgUrl;

        @JsonProperty("rarity")
        private int rarity;
    }
}
