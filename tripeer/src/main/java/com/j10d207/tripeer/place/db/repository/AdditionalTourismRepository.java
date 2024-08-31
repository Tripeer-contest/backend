package com.j10d207.tripeer.place.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.AdditionalTourismEntity;

@Repository
public interface AdditionalTourismRepository extends JpaRepository<AdditionalTourismEntity, Integer>,
    AdditionalRepository<AdditionalTourismEntity> {
    @Override
    Optional<AdditionalTourismEntity> findBySpotInfoId(int spotInfoId);
}
