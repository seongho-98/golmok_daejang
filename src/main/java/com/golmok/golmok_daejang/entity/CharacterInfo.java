package com.golmok.golmok_daejang.entity;

import com.golmok.golmok_daejang.entity.enums.Rarity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "characters")
@Getter @Setter @NoArgsConstructor
public class CharacterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long characterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 이용자ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_number")
    private BusinessInfo businessInfo; // 사업자등록번호

    @Column(name = "character_url", nullable = false)
    private String characterUrl; // 캐릭터URL

    @Column(nullable = false, length = 100)
    private String name; // 이름

    @Column(columnDefinition = "TEXT")
    private String description; // 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity; // 희귀도
}
