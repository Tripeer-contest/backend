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
	private String image;
	private String address;
	private Double latitude;
	private Double longitude;
	private int day;
	private int step;
	private int cost;

	public static HistorySpotRes from(PlanDetailEntity planDetailEntity) {
		return HistorySpotRes.builder()
			.planDetailId(planDetailEntity.getPlanDetailId())
			.title(planDetailEntity.getSpotInfo().getTitle())
			.contentType(ContentTypeEnum.getNameByCode(planDetailEntity.getSpotInfo().getContentTypeId()))
			.image(planDetailEntity.getSpotInfo().getFirstImage())
			.address(planDetailEntity.getSpotInfo().getAddr1())
			.latitude(planDetailEntity.getSpotInfo().getLatitude())
			.longitude(planDetailEntity.getSpotInfo().getLongitude())
			.day(planDetailEntity.getDay())
			.step(planDetailEntity.getStep())
			.cost(planDetailEntity.getCost())
			.build();
	}
}
