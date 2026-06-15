package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.response.BusinessDashboardResponse;
import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.CharacterInfo;
import com.golmok.golmok_daejang.entity.TransactionHistory;
import com.golmok.golmok_daejang.entity.User;
import com.golmok.golmok_daejang.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserRepository userRepository;
    private final BusinessInfoRepository businessInfoRepository;
    private final CharacterInfoRepository characterInfoRepository;
    private final DogamRepository dogamRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final MissionRecordRepository missionRecordRepository;

    @Transactional(readOnly = true)
    public BusinessDashboardResponse getDashboard(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        BusinessInfo business = businessInfoRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("사업자 정보가 존재하지 않습니다."));

        CharacterInfo character = characterInfoRepository.findByBusinessInfo(business).orElse(null);
        long dogamSavedCount = dogamRepository.countByBusinessInfo(business);

        LocalDate today = LocalDate.now();
        int thisYear = today.getYear();
        int thisMonth = today.getMonthValue();
        int lastMonth = thisMonth == 1 ? 12 : thisMonth - 1;
        int lastMonthYear = thisMonth == 1 ? thisYear - 1 : thisYear;

        BigDecimal todayRevenue = transactionHistoryRepository.sumAmountByBusinessAndDate(business, today);
        long todayPaymentCount = transactionHistoryRepository.countByBusinessAndDate(business, today);
        long todayVisitorCount = transactionHistoryRepository.countDistinctUsersByBusinessAndDate(business, today);

        BigDecimal thisMonthRevenue = transactionHistoryRepository.sumAmountByBusinessAndYearMonth(business, thisYear, thisMonth);
        BigDecimal lastMonthRevenue = transactionHistoryRepository.sumAmountByBusinessAndYearMonth(business, lastMonthYear, lastMonth);
        double growthRate = calcGrowthRate(thisMonthRevenue, lastMonthRevenue);

        double revisitRate = calcRevisitRate(business);
        Double avgLikes = missionRecordRepository.findAvgLikesByOwner(user);
        Long totalLikes = missionRecordRepository.findTotalLikesByOwner(user);

        return BusinessDashboardResponse.builder()
                .success(true)
                .character(BusinessDashboardResponse.CharacterSummary.builder()
                        .imageUrl(character != null ? character.getCharacterUrl() : null)
                        .savedCount(dogamSavedCount)
                        .build())
                .data(BusinessDashboardResponse.DashboardData.builder()
                        .today(BusinessDashboardResponse.DashboardData.TodayStats.builder()
                                .date(today)
                                .revenue(todayRevenue)
                                .paymentCount(todayPaymentCount)
                                .visitorCount(todayVisitorCount)
                                .build())
                        .monthly(BusinessDashboardResponse.DashboardData.MonthlyStats.builder()
                                .revenue(thisMonthRevenue)
                                .growthRate(growthRate)
                                .build())
                        .stats(BusinessDashboardResponse.DashboardData.StatsInfo.builder()
                                .revisitRate(revisitRate)
                                .avgLikes(avgLikes != null ? avgLikes : 0.0)
                                .totalLikes(totalLikes != null ? totalLikes : 0L)
                                .build())
                        .build())
                .build();
    }

    private double calcGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return current.subtract(previous)
                .divide(previous, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private double calcRevisitRate(BusinessInfo business) {
        List<TransactionHistory> transactions = transactionHistoryRepository.findByBusinessInfo(business);
        if (transactions.isEmpty()) return 0.0;

        Map<Long, Long> visitCounts = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getUser().getId(), Collectors.counting()));

        long totalUsers = visitCounts.size();
        long revisitingUsers = visitCounts.values().stream().filter(c -> c > 1).count();

        return totalUsers > 0 ? (double) revisitingUsers / totalUsers * 100 : 0.0;
    }
}
