package com.j10d207.tripeer.history.dto.res;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import com.j10d207.tripeer.plan.db.entity.PlanTownEntity;
import com.j10d207.tripeer.user.db.dto.UserSearchDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlanInfoRes {

	private long planId;
	private String title;
	private String img;
	private List<String> townList;
	private LocalDate startDay;
	private LocalDate endDay;
	private List<UserSearchDTO> member;
	private boolean newPlan;

	public static PlanInfoRes from(PlanEntity planEntity) {
		List<PlanTownEntity> planTown = planEntity.getPlanTown();
		List<String> townNameList = new ArrayList<>();
		for (PlanTownEntity planTownEntity : planTown) {
			if (planTownEntity.getTown() == null) {
				townNameList.add(planTownEntity.getCityOnly().getCityName());
			} else {
				townNameList.add(planTownEntity.getTown().getTownName());
			}
		}
		String img;
		if (planTown.getFirst().getTown() == null) {
			img = planTown.getFirst().getCityOnly().getCityImg();
		} else {
			img = planTown.getFirst().getTown().getTownImg();
		}
		return PlanInfoRes.builder()
			.planId(planEntity.getPlanId())
			.title(planEntity.getTitle())
			.img(img)
			.townList(townNameList)
			.startDay(planEntity.getStartDate())
			.endDay(planEntity.getEndDate())
			.member(planEntity.getCoworkerList().stream().map(UserSearchDTO::from).toList())
			.newPlan(false)
			.build();
	}
}
