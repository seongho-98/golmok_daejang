package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginData {

    @JsonProperty("user")
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {

        @JsonProperty("loginId")
        private String loginId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type; // "개인" or "사업자"

        @JsonProperty("businessNumber")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String businessNumber; // 사업자 로그인 시에만 포함
    }
}
