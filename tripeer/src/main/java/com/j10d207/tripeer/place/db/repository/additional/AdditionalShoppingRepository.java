package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalShoppingEntity;

@Repository
public interface AdditionalShoppingRepository extends JpaRepository<AdditionalShoppingEntity, Integer>,
    AdditionalRepository<AdditionalShoppingEntity> {
    @Override
    Optional<AdditionalShoppingEntity> findBySpotInfoId(int spotInfoId);
}
