package com.j10d207.tripeer.noti.service;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;

public class CompletePlanEvent {
	private final PlanEntity plan;

	public CompletePlanEvent(final PlanEntity plan) {
		this.plan = plan;
	}

	public PlanEntity getPlan() {
		return plan;
	}
}
