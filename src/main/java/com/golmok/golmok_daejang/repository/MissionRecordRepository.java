package com.golmok.golmok_daejang.repository;

import com.golmok.golmok_daejang.entity.MissionRecord;
import com.golmok.golmok_daejang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRecordRepository extends JpaRepository<MissionRecord, Long> {

    @Query("SELECT COALESCE(AVG(m.likes), 0) FROM MissionRecord m WHERE m.owner = :owner")
    Double findAvgLikesByOwner(@Param("owner") User owner);

    @Query("SELECT COALESCE(SUM(m.likes), 0) FROM MissionRecord m WHERE m.owner = :owner")
    Long findTotalLikesByOwner(@Param("owner") User owner);
}
