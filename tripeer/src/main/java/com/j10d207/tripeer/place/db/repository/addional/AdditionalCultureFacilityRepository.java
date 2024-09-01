package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalCultureFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalCultureFacilityRepository extends JpaRepository<AdditionalCultureFacilityEntity, Integer> {
    AdditionalCultureFacilityEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
