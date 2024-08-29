package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.user.dto.res.UserDTO;

import lombok.Getter;

@Getter
public class PlanNodeTempleDTO {
	private PlanDetailMainDTO.CreateResultInfo planInfo;
	private UserDTO.Search userInfo;

	public PlanNodeTempleDTO(PlanDetailMainDTO.CreateResultInfo planInfo, UserDTO.Search userInfo) {
		this.planInfo = planInfo;
		this.userInfo = userInfo;
	}
}
