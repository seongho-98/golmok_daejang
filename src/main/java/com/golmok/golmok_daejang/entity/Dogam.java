package com.golmok.golmok_daejang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "dogam")
@IdClass(DogamId.class)
@Getter @Setter @NoArgsConstructor
public class Dogam {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 이용자ID

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_number")
    private BusinessInfo businessInfo; // 사업자등록번호

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate; // 수집일
}
