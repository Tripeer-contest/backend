package com.j10d207.tripeer.plan.dto.res;

import java.util.List;

import com.j10d207.tripeer.plan.db.dto.TownDTO;
import com.j10d207.tripeer.user.dto.res.UserDTO;

import lombok.Getter;

@Getter
public class PlanNodeOutDTO {
	private long userId;
	private long planId;

	public PlanNodeOutDTO(long userId, long planId) {
		this.userId = userId;
		this.planId = planId;
	}
}
