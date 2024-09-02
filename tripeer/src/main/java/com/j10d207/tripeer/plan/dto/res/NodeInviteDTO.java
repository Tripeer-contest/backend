package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.dto.res.UserDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NodeInviteDTO {
	private Long planId;
	private UserDTO.Search userInfo;

	public static NodeInviteDTO from(UserEntity user, PlanEntity plan) {
		return NodeInviteDTO.builder()
			.planId(plan.getPlanId())
			.userInfo(UserDTO.Search.fromUserEntity(user))
			.build();
	}
}
