package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginData {

    @JsonProperty("사용자")
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {

        @JsonProperty("아이디")
        private String loginId;

        @JsonProperty("이름")
        private String name;

        @JsonProperty("타입")
        private String type; // "개인" or "사업자"

        @JsonProperty("사업자번호")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String businessNumber; // 사업자 로그인 시에만 포함
    }
}
