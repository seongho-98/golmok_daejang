package com.golmok.golmok_daejang.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class DogamId implements Serializable {
    private Long user;           // User.userId 타입과 일치
    private String businessInfo; // BusinessInfo.businessNumber 타입과 일치
}
