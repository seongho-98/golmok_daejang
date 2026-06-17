package com.golmok.golmok_daejang.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "business_info")
@Getter @Setter @NoArgsConstructor
public class BusinessInfo {

    @Id
    @Column(name = "business_number", length = 12) // 형식: 000-00-00000
    private String businessNumber; // 사업자등록번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 이용자ID

    @Column(nullable = false)
    private String address; // 소재지

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName; // 업체명

    @Column(name = "business_type", nullable = false, length = 100)
    private String businessType; // 업태명

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude; // 위도

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude; // 경도
}
