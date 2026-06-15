package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByLoginIdAndPassword(String loginId, String password);
    Optional<User> findByResidentNumber(String residentNumber);
    boolean existsByLoginId(String loginId);
    boolean existsByResidentNumber(String residentNumber);
}
