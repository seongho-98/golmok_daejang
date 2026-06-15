package com.golmok.golmok_daejang.entity;

import com.golmok.golmok_daejang.entity.enums.MissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "mission_records")
@Getter @Setter @NoArgsConstructor
public class MissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long missionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 이용자ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // 사장님ID

    @Column(nullable = false)
    private LocalDate date; // 날짜

    @Column(nullable = false)
    private Integer sequence; // 순서

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status; // 상태 (진행중/취소/성공)

    @Column(nullable = false)
    private Integer likes = 0; // 좋아요
}
