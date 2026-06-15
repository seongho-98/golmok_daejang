package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessInfoRepository extends JpaRepository<BusinessInfo, String> {
    Optional<BusinessInfo> findByUser(User user);
}
