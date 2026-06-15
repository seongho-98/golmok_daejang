package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserProfileData {

    @JsonProperty("아이디")
    private String loginId;

    @JsonProperty("성명")
    private String name;

    @JsonProperty("타입")
    private String type;

    @JsonProperty("희귀1")
    private List<CharacterItem> rarity1;

    @JsonProperty("희귀2")
    private List<CharacterItem> rarity2;

    @JsonProperty("희귀3")
    private List<CharacterItem> rarity3;

    @JsonProperty("가입일")
    private LocalDate createdAt;

    @Getter
    @Builder
    public static class CharacterItem {

        @JsonProperty("imgUrl")
        private String imgUrl;

        @JsonProperty("캐릭터명")
        private String characterName;

        @JsonProperty("가게명")
        private String businessName;
    }
}
