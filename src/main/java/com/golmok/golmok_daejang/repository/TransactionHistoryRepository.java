package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findByBusinessInfo(BusinessInfo businessInfo);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionHistory t WHERE t.businessInfo = :business AND t.date = :date")
    BigDecimal sumAmountByBusinessAndDate(@Param("business") BusinessInfo business, @Param("date") LocalDate date);

    @Query("SELECT COUNT(t) FROM TransactionHistory t WHERE t.businessInfo = :business AND t.date = :date")
    long countByBusinessAndDate(@Param("business") BusinessInfo business, @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT t.user) FROM TransactionHistory t WHERE t.businessInfo = :business AND t.date = :date")
    long countDistinctUsersByBusinessAndDate(@Param("business") BusinessInfo business, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionHistory t WHERE t.businessInfo = :business AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    BigDecimal sumAmountByBusinessAndYearMonth(@Param("business") BusinessInfo business, @Param("year") int year, @Param("month") int month);

    @Query("SELECT DISTINCT t.businessInfo FROM TransactionHistory t WHERE t.user.residentNumber = :residentNumber")
    List<BusinessInfo> findDistinctBusinessesByResidentNumber(@Param("residentNumber") String residentNumber);
}
