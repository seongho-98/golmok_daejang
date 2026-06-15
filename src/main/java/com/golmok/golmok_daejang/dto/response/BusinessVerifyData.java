package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessVerifyData {

    @JsonProperty("businesses")
    private List<BusinessItem> businesses;

    @Getter
    @Builder
    public static class BusinessItem {

        @JsonProperty("businessName")
        private String businessName;

        @JsonProperty("businessNumber")
        private String businessNumber;

        @JsonProperty("address")
        private String address;

        @JsonProperty("businessType")
        private String businessType;

        @JsonProperty("ownerName")
        private String ownerName;

        @JsonProperty("openDate")
        private String openDate; // 외부 API 연동 전 null 반환
    }
}
