package com.golmok.golmok_daejang.entity;

import com.golmok.golmok_daejang.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "resident_number", unique = true, nullable = false, length = 14)
    private String residentNumber; // 주민번호 (형식: 000000-0000000)

    @Column(nullable = false, length = 50)
    private String name; // 이름

    @Column(nullable = false)
    private String password; // 비번

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 역할
}
