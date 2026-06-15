package com.golmok.golmok_daejang.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

// 명세 구조상 success/캐릭터/data가 동일 레벨이라 ApiResponse 미사용
@Getter
@Builder
public class BusinessDashboardResponse {

    private boolean success;

    @JsonProperty("캐릭터")
    private CharacterSummary character;

    private DashboardData data;

    @Getter
    @Builder
    public static class CharacterSummary {

        @JsonProperty("이미지주소")
        private String imageUrl;

        @JsonProperty("도감저장수")
        private long savedCount;
    }

    @Getter
    @Builder
    public static class DashboardData {

        private TodayStats today;
        private MonthlyStats monthly;
        private StatsInfo stats;

        @Getter
        @Builder
        public static class TodayStats {

            private LocalDate date;

            @JsonProperty("매출")
            private BigDecimal revenue;

            @JsonProperty("결제건수")
            private long paymentCount;

            @JsonProperty("방문자수")
            private long visitorCount;
        }

        @Getter
        @Builder
        public static class MonthlyStats {

            @JsonProperty("매출")
            private BigDecimal revenue;

            @JsonProperty("전달대비상승률")
            private double growthRate;
        }

        @Getter
        @Builder
        public static class StatsInfo {

            @JsonProperty("재방문율")
            private double revisitRate;

            @JsonProperty("평균좋아요")
            private double avgLikes;

            @JsonProperty("좋아요수")
            private long totalLikes;
        }
    }
}
