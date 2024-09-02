package com.j10d207.tripeer.place.db.repository.addional;

import com.j10d207.tripeer.place.db.entity.additional.AdditionalTourCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalTourCourseRepository extends JpaRepository<AdditionalTourCourseEntity, Integer> {
    AdditionalTourCourseEntity findBySpotInfo_SpotInfoId(int spotInfoId);
}
