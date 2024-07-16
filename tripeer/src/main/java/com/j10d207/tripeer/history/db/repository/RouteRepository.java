package com.j10d207.tripeer.history.db.repository;

import com.j10d207.tripeer.history.db.entity.RouteEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    public RouteEntity findByPlanDetail(PlanDetailEntity planDetail);
}
