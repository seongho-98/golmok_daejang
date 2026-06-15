package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserProfileData {

    @JsonProperty("loginId")
    private String loginId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("rarity1")
    private List<CharacterItem> rarity1;

    @JsonProperty("rarity2")
    private List<CharacterItem> rarity2;

    @JsonProperty("rarity3")
    private List<CharacterItem> rarity3;

    @JsonProperty("createdAt")
    private LocalDate createdAt;

    @Getter
    @Builder
    public static class CharacterItem {

        @JsonProperty("imgUrl")
        private String imgUrl;

        @JsonProperty("characterName")
        private String characterName;

        @JsonProperty("businessName")
        private String businessName;
    }
}
