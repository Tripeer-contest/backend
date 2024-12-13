package com.j10d207.tripeer.history.db.entity;

import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "route")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// PK
	private long routeId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_DETAIL_ID")
	private PlanDetailEntity planDetail;

	private Integer day;
	private Integer totalFare;
	private String pathType;

}
