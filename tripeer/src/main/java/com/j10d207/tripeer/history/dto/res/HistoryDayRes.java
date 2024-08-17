package com.j10d207.tripeer.history.dto.res;

import java.util.ArrayList;
import java.util.List;

import com.j10d207.tripeer.plan.db.entity.PlanDayEntity;
import com.j10d207.tripeer.plan.db.entity.PlanDetailEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryDayRes {
	private long planDayId;
	private String date;
	// 각 날짜 별로 4개의 미리보기 이미지
	private List<String> galleryImgList;
	// 각 날짜 별로 어느 관광지에 갔었는지의 리스트
	private List<HistorySpotRes> planDetailList;
	// 각 여행지 사이의 이동 시간 리스트
	private List<List<String>> timeList;
	// 각 여행지 사이를 어떻게 갔는지에 대한 경로 정보
	private List<RouteRes> routeList;

	public static HistoryDayRes from(PlanDayEntity planDayEntity) {
		List<PlanDetailEntity> planDetailEntityList = planDayEntity.getPlanDetailList();
		return HistoryDayRes.builder()
			.planDayId(planDayEntity.getPlanDayId())
			.date(planDayEntity.getDay().toString())
			.galleryImgList(new ArrayList<>())
			.timeList(planDetailEntityList.stream().map(HistoryDayRes::makeTimeList).toList())
			.routeList(planDetailEntityList.stream().map(RouteRes::from).toList())
			.build();
	}

	//타임리스트를 만드는 함수
	private static List<String> makeTimeList(PlanDetailEntity planDetail) {
		List<String> timeList = new ArrayList<>();
		if (planDetail.getDescription().equals("자동차")) {
			timeList.add(planDetail.getSpotTime().toString());
			timeList.add("0");
		} else if (planDetail.getDescription().equals("대중교통")) {
			timeList.add(planDetail.getSpotTime().toString());
			timeList.add("1");
		}
		return timeList;
	}
}
