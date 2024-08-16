package com.j10d207.tripeer.history.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistorySpotRes {
	private long planDetailId;
	private String title;
	private String contentType;
	private String address;
	private String image;
	private int day;
	private int step;
	private int cost;

	public static HistorySpotRes from(PlanDetailEntity planDetailEntity) {
		return HistorySpotRes.builder()
			.planDetailId(planDetailEntity.getPlanDetailId())
			.title(planDetailEntity.getSpotInfo().getTitle())
			.planDetailId(planDetailEntity.getSpotInfo().getSpotInfoId())
			.contentType(ContentTypeEnum.getNameByCode(planDetailEntity.getSpotInfo().getContentTypeId()))
			.address(planDetailEntity.getSpotInfo().getAddr1())
			.image(planDetailEntity.getSpotInfo().getFirstImage())
			.day(planDetailEntity.getDay())
			.step(planDetailEntity.getStep())
			.cost(planDetailEntity.getCost())
			.build();
	}
}
