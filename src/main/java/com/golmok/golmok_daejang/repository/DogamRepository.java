package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.Dogam;
import com.golmok.golmok_daejang.entity.DogamId;
import com.golmok.golmok_daejang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogamRepository extends JpaRepository<Dogam, DogamId> {
    List<Dogam> findByUser(User user);
    boolean existsByUserAndBusinessInfo(User user, BusinessInfo businessInfo);
    long countByBusinessInfo(BusinessInfo businessInfo);
}
