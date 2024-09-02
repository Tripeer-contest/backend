package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalShoppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalShoppingRepository extends JpaRepository<AdditionalShoppingEntity, Integer> {
    AdditionalShoppingEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
