package com.j10d207.tripeer.history.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.history.db.entity.GalleryEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;

@Repository
public interface GalleryRepository extends JpaRepository<GalleryEntity, Long> {

	List<GalleryEntity> findAllByPlanDay(PlanDayEntity planDay);
}
