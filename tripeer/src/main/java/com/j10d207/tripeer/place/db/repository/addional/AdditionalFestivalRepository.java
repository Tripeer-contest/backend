package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFestivalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalFestivalRepository extends JpaRepository<AdditionalFestivalEntity, Integer> {
    AdditionalFestivalEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
