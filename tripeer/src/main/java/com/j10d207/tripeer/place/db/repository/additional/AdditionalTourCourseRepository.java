package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalTourCourseEntity;

@Repository
public interface AdditionalTourCourseRepository extends JpaRepository<AdditionalTourCourseEntity, Integer>,
    AdditionalRepository<AdditionalTourCourseEntity> {
    Optional<AdditionalTourCourseEntity> findBySpotInfoId(int spotInfoId);
}
