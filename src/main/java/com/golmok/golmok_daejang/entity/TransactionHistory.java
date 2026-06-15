package com.golmok.golmok_daejang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transaction_history")
@Getter @Setter @NoArgsConstructor
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    // 주민번호 기준으로 사용자 참조 (거래 시점 기록 목적)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_number", referencedColumnName = "resident_number")
    private User user; // 주민번호

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal amount; // 금액 (원화)

    @Column(nullable = false)
    private LocalDate date; // 날짜

    @Column(nullable = false)
    private LocalTime time; // 시간

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName; // 업체명 (거래 시점 스냅샷)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_number")
    private BusinessInfo businessInfo; // 사업자등록번호

    @Column(nullable = false)
    private String address; // 주소 (거래 시점 스냅샷)

    @Column(name = "business_type", nullable = false, length = 100)
    private String businessType; // 업태명 (거래 시점 스냅샷)
}
