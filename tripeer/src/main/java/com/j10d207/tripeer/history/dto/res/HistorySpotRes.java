package com.j10d207.tripeer.history.dto.res;

import com.j10d207.tripeer.place.db.ContentTypeEnum;
import com.j10d207.tripeer.place.db.entity.SpotReviewEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistorySpotRes {
	private long planDetailId;
	private long spotId;
	private String title;
	private String contentType;
	private String image;
	private String address;
	private Double latitude;
	private Double longitude;
	private Double starPointAvg;
	private boolean isWriteReview;
	private int day;
	private int step;
	private int cost;

	public static HistorySpotRes from(PlanDetailEntity planDetailEntity, UserEntity userEntity) {
		return HistorySpotRes.builder()
			.planDetailId(planDetailEntity.getPlanDetailId())
			.spotId(planDetailEntity.getSpotInfo().getSpotInfoId())
			.title(planDetailEntity.getSpotInfo().getTitle())
			.contentType(ContentTypeEnum.getNameByCode(planDetailEntity.getSpotInfo().getContentTypeId()))
			.image(planDetailEntity.getSpotInfo().getFirstImage())
			.address(planDetailEntity.getSpotInfo().getAddr1())
			.latitude(planDetailEntity.getSpotInfo().getLatitude())
			.longitude(planDetailEntity.getSpotInfo().getLongitude())
			.starPointAvg(planDetailEntity.getSpotInfo().getStarPointAvg())
			// .isWriteReview(planDetailEntity.getSpotInfo().getSpotReviewList().stream().map(SpotReviewEntity::getUser).anyMatch(userEntity::equals))
			.day(planDetailEntity.getDay())
			.step(planDetailEntity.getStep())
			.cost(planDetailEntity.getCost())
			.build();
	}
}
