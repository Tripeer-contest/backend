package com.j10d207.tripeer.plan.db.repository;

import com.j10d207.tripeer.plan.db.entity.PlanBucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PlanBucketRepository extends JpaRepository<PlanBucketEntity, Long> {

    Boolean existsByPlan_PlanIdAndSpotInfo_SpotInfoId(long planId, int spotInfoId);
    Optional<PlanBucketEntity> findByPlan_PlanIdAndSpotInfo_SpotInfoId(long planId, int spotInfoId);

    @Query("SELECT b.spotInfo.spotInfoId FROM plan_bucket b WHERE b.user.userId = :userId")
    Set<Integer> findAllSpotInfoIdsByUserId(@Param("userId") long userId);
}
