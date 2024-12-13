package com.j10d207.tripeer.place.db.repository;

import com.j10d207.tripeer.place.db.entity.SpotInfoEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotInfoRepository extends JpaRepository <SpotInfoEntity, Integer > {

    List<SpotInfoEntity> findByContentTypeIdAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(int contentTypeId, int cityId, int townId, Pageable pageable);

    List<SpotInfoEntity> findByContentTypeIdNotInAndTown_TownPK_City_CityIdAndTown_TownPK_TownId(List<Integer> contentTypeIds, int cityId, int townId, Pageable pageable);

    List<SpotInfoEntity> findByContentTypeIdAndTown_TownPK_City_CityId(Integer contentTypeId, Integer cityId, Pageable pageable);

    List<SpotInfoEntity> findByContentTypeIdNotInAndTown_TownPK_City_CityId(List<Integer> contentTypeId, Integer cityId, Pageable pageable);

    List<SpotInfoEntity> findByContentTypeId(Integer contentTypeId, Pageable pageable);

    List<SpotInfoEntity> findByContentTypeIdNotIn(List<Integer> contentTypeId, Pageable pageable);

    SpotInfoEntity findBySpotInfoId(int spotInfoId);

    @Query("SELECT s FROM spot_info s " +
            "JOIN plan_town p " +
            "ON p.plan.planId = :planId AND (p.cityOnly.cityId = s.town.townPK.city.cityId OR (p.town.townPK.city.cityId = s.town.townPK.city.cityId AND p.town.townPK.townId = s.town.townPK.townId)) " +
            "WHERE s.title LIKE %:keyword% " +
            "AND (:sortType = 1 OR " +                                                    // sortType 1 이면 전체
            "    (:sortType = 2 AND s.contentTypeId IN (12, 14, 15, 25, 28, 38)) OR " +   // sortType 2 이면 명소
            "    (:sortType = 3 AND s.contentTypeId = 32) OR " +                          // sortType 3 이면 숙소
            "    (:sortType = 4 AND s.contentTypeId = 39)) " +                             // sortType 4 이면 음식점
            "AND (:cityId = -1 OR s.town.townPK.city.cityId = :cityId) " +
            "AND (:townId = -1 OR s.town.townPK.townId = :townId)")
    Page<SpotInfoEntity> searchSpotsOfOption(@Param("planId") long planId, @Param("keyword") String keyword, @Param("sortType") int sortType, @Param("cityId") int cityId, @Param("townId") int townId, Pageable pageable);


    @Query("SELECT s FROM spot_info s WHERE " +
        "s.latitude BETWEEN :minLat AND :maxLat " +                                   // 위도에 포함되고
        "AND s.longitude BETWEEN :minLon AND :maxLon " +                              // 경도에 포함되고
        "AND (:sortType = 1 OR " +                                                    // sortType 1 이면 전체
        "    (:sortType = 2 AND s.contentTypeId IN (12, 14, 15, 25, 28, 38)) OR " +   // sortType 2 이면 명소
        "    (:sortType = 3 AND s.contentTypeId = 32) OR " +                          // sortType 3 이면 숙소
        "    (:sortType = 4 AND s.contentTypeId = 39))" +                             // sortType 4 이면 음식점
        "AND s.title LIKE %:keyword%")                                                // 키워드를 포함하는
    Page<SpotInfoEntity> searchSpotsInMap(@Param("minLat") double minLat, @Param("maxLat") double maxLat,@Param("minLon") double minLon,
                                          @Param("maxLon") double maxLon, @Param("keyword") String keyword, @Param("sortType") int sortType,
                                           Pageable pageable);

    @Query("SELECT s FROM spot_info s WHERE " +
            "s.latitude BETWEEN :minLat AND :maxLat " +
            "AND s.longitude BETWEEN :minLon AND :maxLon " +
            "AND s.title LIKE %:title%")
    Optional<SpotInfoEntity> searchNearSpot(@Param("title") String title,
                                            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
                                            @Param("minLon") double minLon, @Param("maxLon") double maxLon);

    @Query("SELECT s FROM spot_info s LEFT JOIN FETCH s.spotReviewList WHERE s.spotInfoId IN :ids")
    List<SpotInfoEntity> findAllWithReviewsById(@Param("ids") List<Integer> ids);
}
