package com.j10d207.tripeer.history.dto.res;

import java.util.List;

import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryDayRes {
	private long planDayId;
	private String date;
	// 각 날짜 별로 어느 관광지에 갔었는지의 리스트
	private List<HistorySpotRes> planDetailList;

	public static HistoryDayRes from(PlanDayEntity planDayEntity, List<Integer> reviewIdList) {
		return HistoryDayRes.builder()
			.planDayId(planDayEntity.getPlanDayId())
			.date(planDayEntity.getDay().toString())
			.planDetailList(planDayEntity.getPlanDetailList().stream().map(el ->
				HistorySpotRes.from(el, reviewIdList.contains(el.getSpotInfo().getSpotInfoId()))).toList())
			.build();
	}

}
