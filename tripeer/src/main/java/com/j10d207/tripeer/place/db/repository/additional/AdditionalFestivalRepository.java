package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFestivalEntity;

@Repository
public interface AdditionalFestivalRepository extends JpaRepository<AdditionalFestivalEntity, Integer>,
    AdditionalRepository<AdditionalFestivalEntity> {
    Optional<AdditionalFestivalEntity> findBySpotInfoId(int spotInfoId);
}
