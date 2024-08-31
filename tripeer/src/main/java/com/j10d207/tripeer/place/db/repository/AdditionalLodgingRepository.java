package com.j10d207.tripeer.place.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.AdditionalLodgingEntity;

@Repository
public interface AdditionalLodgingRepository extends JpaRepository<AdditionalLodgingEntity, Integer>,
    AdditionalRepository<AdditionalLodgingEntity> {
    Optional<AdditionalLodgingEntity> findBySpotInfoId(int spotInfoId);
}
