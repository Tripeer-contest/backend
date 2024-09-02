package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;
import com.j10d207.tripeer.place.db.entity.additional.AdditionalLeportsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalLeportsRepository extends JpaRepository<AdditionalLeportsEntity, Integer> {
    AdditionalLeportsEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}

