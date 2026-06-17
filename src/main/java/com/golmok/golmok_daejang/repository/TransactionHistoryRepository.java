package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.dto.BusinessTypeTransitionRow;
import com.golmok.golmok_daejang.entity.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query(value = """
        SELECT
            business_type AS currentType,
            LEAD(business_type, 1) OVER (
                PARTITION BY resident_number, date
                ORDER BY time ASC
            ) AS nextType,
            date AS date,
            time AS time
        FROM transaction_history
        WHERE resident_number = :residentNumber
          AND date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
          AND WEEKDAY(date) = WEEKDAY(CURDATE())
          AND time >= TIME(NOW())
          AND time <= ADDTIME(TIME(NOW()), '03:00:00')
        ORDER BY date, time ASC
        """, nativeQuery = true)
    List<BusinessTypeTransitionRow> findBusinessTypeTransitions(@Param("residentNumber") String residentNumber);

    List<TransactionHistory> findByBusinessNumber(String businessNumber);

    @Query("SELECT DISTINCT t.businessNumber FROM TransactionHistory t WHERE t.residentNumber = :residentNumber")
    List<String> findDistinctBusinessNumbersByResidentNumber(@Param("residentNumber") String residentNumber);

    @Query(value = """
        SELECT DISTINCT business_name
        FROM transaction_history
        WHERE resident_number = :residentNumber
          AND business_type = :businessType
          AND date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
          AND WEEKDAY(date) = WEEKDAY(CURDATE())
          AND time >= TIME(NOW())
          AND time <= ADDTIME(TIME(NOW()), '03:00:00')
        """, nativeQuery = true)
    List<String> findStoreNamesByTypeAndTimeFilter(
            @Param("residentNumber") String residentNumber,
            @Param("businessType") String businessType
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionHistory t WHERE t.businessNumber = :businessNumber AND t.date = :date")
    BigDecimal sumAmountByBusinessNumberAndDate(@Param("businessNumber") String businessNumber, @Param("date") LocalDate date);

    @Query("SELECT COUNT(t) FROM TransactionHistory t WHERE t.businessNumber = :businessNumber AND t.date = :date")
    long countByBusinessNumberAndDate(@Param("businessNumber") String businessNumber, @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT t.residentNumber) FROM TransactionHistory t WHERE t.businessNumber = :businessNumber AND t.date = :date")
    long countDistinctResidentsByBusinessNumberAndDate(@Param("businessNumber") String businessNumber, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionHistory t WHERE t.businessNumber = :businessNumber AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    BigDecimal sumAmountByBusinessNumberAndYearMonth(@Param("businessNumber") String businessNumber, @Param("year") int year, @Param("month") int month);
}
