package com.j10d207.tripeer.noti.service;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

public class InviteCoworkerEvent {

	private final UserEntity invitedCoworker;

	private final PlanEntity targetPlan;

	public InviteCoworkerEvent(final UserEntity invitedCoworker, final PlanEntity targetPlan) {
		this.invitedCoworker = invitedCoworker;
		this.targetPlan = targetPlan;
	}
}
