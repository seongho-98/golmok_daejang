package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.CharacterInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CharacterInfoRepository extends JpaRepository<CharacterInfo, Long> {
    Optional<CharacterInfo> findByBusinessInfo(BusinessInfo businessInfo);
}
