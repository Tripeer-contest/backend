package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalTourismEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalTourismRepository extends JpaRepository<AdditionalTourismEntity, Integer> {
    AdditionalTourismEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
