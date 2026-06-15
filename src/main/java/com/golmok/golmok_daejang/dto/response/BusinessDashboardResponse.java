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

    @JsonProperty("character")
    private CharacterSummary character;

    private DashboardData data;

    @Getter
    @Builder
    public static class CharacterSummary {

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("savedCount")
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

            @JsonProperty("revenue")
            private BigDecimal revenue;

            @JsonProperty("paymentCount")
            private long paymentCount;

            @JsonProperty("visitorCount")
            private long visitorCount;
        }

        @Getter
        @Builder
        public static class MonthlyStats {

            @JsonProperty("revenue")
            private BigDecimal revenue;

            @JsonProperty("growthRate")
            private double growthRate;
        }

        @Getter
        @Builder
        public static class StatsInfo {

            @JsonProperty("revisitRate")
            private double revisitRate;

            @JsonProperty("avgLikes")
            private double avgLikes;

            @JsonProperty("totalLikes")
            private long totalLikes;
        }
    }
}
