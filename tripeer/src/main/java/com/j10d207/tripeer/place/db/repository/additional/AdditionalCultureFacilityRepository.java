package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.j10d207.tripeer.place.db.entity.additional.AdditionalCultureFacilityEntity;

@Repository
public interface AdditionalCultureFacilityRepository extends JpaRepository<AdditionalCultureFacilityEntity, Integer>,
    AdditionalRepository<AdditionalCultureFacilityEntity> {
    @Override
    Optional<AdditionalCultureFacilityEntity> findBySpotInfoId(int spotInfoId);
}
