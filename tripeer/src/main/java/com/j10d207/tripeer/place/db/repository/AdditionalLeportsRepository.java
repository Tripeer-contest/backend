package com.j10d207.tripeer.place.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.AdditionalLeportsEntity;

@Repository
public interface AdditionalLeportsRepository extends JpaRepository<AdditionalLeportsEntity, Integer>,
    AdditionalRepository<AdditionalLeportsEntity> {
    Optional<AdditionalLeportsEntity> findBySpotInfoId(int spotInfoId);
}
