package com.golmok.golmok_daejang.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Rarity {
    COMMON(1),    // 일반
    RARE(2),      // 레어
    LEGENDARY(3); // 전설

    private final int level;

    Rarity(int level) {
        this.level = level;
    }

    public static Rarity of(int level) {
        return Arrays.stream(values())
                .filter(r -> r.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 희귀도: " + level));
    }
}
