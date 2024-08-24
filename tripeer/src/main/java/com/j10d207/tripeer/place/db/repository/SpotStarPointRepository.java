package com.j10d207.tripeer.place.db.repository;

import com.j10d207.tripeer.place.db.entity.SpotStarPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotStarPointRepository extends JpaRepository<SpotStarPointEntity, Long > {
    List<SpotStarPointEntity> findBySpotInfo_SpotInfoId(int spotInfoId);
    List<SpotStarPointEntity> findBySpotInfo_SpotInfoIdAndUser_UserId(int spotInfoId, long userId);
}
