package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessVerifyData {

    @JsonProperty("개인사업자정보")
    private List<BusinessItem> businesses;

    @Getter
    @Builder
    public static class BusinessItem {

        @JsonProperty("업체명")
        private String businessName;

        @JsonProperty("사업자번호")
        private String businessNumber;

        @JsonProperty("소재지")
        private String address;

        @JsonProperty("업태명")
        private String businessType;

        @JsonProperty("대표자명")
        private String ownerName;

        @JsonProperty("개업일")
        private String openDate; // 외부 API 연동 전 null 반환
    }
}
