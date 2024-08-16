package com.j10d207.tripeer.history.dto.res;

import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CostRes {
	private long planDetailId;
	private int cost;

	public static CostRes from(PlanDetailEntity planDetailEntity) {
		return CostRes.builder()
			.planDetailId(planDetailEntity.getPlanDetailId())
			.cost(planDetailEntity.getCost())
			.build();
	}
}
