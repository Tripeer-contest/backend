package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalFoodRepository extends JpaRepository<AdditionalFoodEntity, Integer> {
    AdditionalFoodEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}