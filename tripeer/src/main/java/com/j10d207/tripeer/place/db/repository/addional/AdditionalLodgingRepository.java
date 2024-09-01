package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalLodgingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalLodgingRepository extends JpaRepository<AdditionalLodgingEntity, Integer> {
    AdditionalLodgingEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
