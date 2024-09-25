package com.j10d207.tripeer.plan.db.repository;

import java.time.LocalDate;
import java.util.List;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

    PlanEntity findByPlanId(long planId);

    @Query("SELECT p.planId FROM plan p where p.isSaved = false AND p.endDate < :today ")
    List<Long> findAllWithUnsavedAndPastEndDate(@Param("today") LocalDate today);

    @Query("SELECT p FROM plan p " +
        "LEFT JOIN FETCH p.planDayList pd " +
        "LEFT JOIN FETCH pd.planDetailList pdl " +
        "LEFT JOIN FETCH pdl.spotInfo si " +
        "WHERE p.planId = :planId")
    PlanEntity findByPlanForHistory(@Param("planId") long planId);
}

