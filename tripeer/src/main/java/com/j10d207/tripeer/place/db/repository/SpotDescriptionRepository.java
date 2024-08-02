package com.j10d207.tripeer.place.db.repository;

import com.j10d207.tripeer.place.db.entity.SpotDescriptionEntity;
import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotDescriptionRepository extends JpaRepository<SpotDescriptionEntity, Integer> {
    SpotDescriptionEntity findBySpotInfo(SpotInfoEntity spotInfoEntity);
}