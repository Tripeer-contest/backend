package com.j10d207.tripeer.plan.dto.res;

import java.util.List;

import com.j10d207.tripeer.plan.db.dto.TownDTO;
import com.j10d207.tripeer.user.dto.res.UserDTO;

import lombok.Getter;

@Getter
public class PlanNodeTempleDTO {
	private PlanDetailMainDTO.CreateResultInfo planInfo;
	private UserDTO.Search userInfo;
	private List<TownDTO> townDTOList;

	public PlanNodeTempleDTO(PlanDetailMainDTO.CreateResultInfo planInfo,
							UserDTO.Search userInfo,
							List<TownDTO> townDTOList) {
		this.planInfo = planInfo;
		this.userInfo = userInfo;
		this.townDTOList = townDTOList;
	}
}
