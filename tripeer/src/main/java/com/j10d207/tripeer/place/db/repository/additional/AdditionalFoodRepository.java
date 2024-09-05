package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalFoodEntity;

@Repository
public interface AdditionalFoodRepository extends JpaRepository<AdditionalFoodEntity, Integer>,
    AdditionalRepository<AdditionalFoodEntity> {
    Optional<AdditionalFoodEntity> findBySpotInfoId(int spotInfoId);
}
