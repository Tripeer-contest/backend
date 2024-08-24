package com.j10d207.tripeer.place.db.repository;

import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotReviewRepository extends JpaRepository<SpotReviewEntity, Long > {
    List<SpotReviewEntity> findBySpotInfo_SpotInfoId(int spotInfoId);
    List<SpotReviewEntity> findBySpotInfo_SpotInfoIdAndUser_UserId(int spotInfoId, long userId);
//    Optional<Double> findAverageStarPointBySpotInfo_SpotInfoId(long spotInfoId);
    @Query(value = "SELECT AVG(star_point) FROM tripeer.spot_review WHERE spot_info_id = :spotInfoId", nativeQuery = true)
    Double findAverageStarPointBySpotInfoId(@Param("spotInfoId") Long spotInfoId);
}
